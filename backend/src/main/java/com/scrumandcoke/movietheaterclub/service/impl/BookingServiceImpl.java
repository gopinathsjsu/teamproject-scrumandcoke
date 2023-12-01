package com.scrumandcoke.movietheaterclub.service.impl;

import com.google.common.eventbus.EventBus;
import com.scrumandcoke.movietheaterclub.dto.BookingDto;
import com.scrumandcoke.movietheaterclub.dto.ShowTimeDto;
import com.scrumandcoke.movietheaterclub.entity.BookingEntity;
import com.scrumandcoke.movietheaterclub.entity.ShowTimeEntity;
import com.scrumandcoke.movietheaterclub.enums.BookingStatus;
import com.scrumandcoke.movietheaterclub.enums.MemberType;
import com.scrumandcoke.movietheaterclub.enums.PaymentMethod;
import com.scrumandcoke.movietheaterclub.enums.TransactionType;
import com.scrumandcoke.movietheaterclub.exception.GlobalException;
import com.scrumandcoke.movietheaterclub.mapper.BookingMapper;
import com.scrumandcoke.movietheaterclub.repository.BookingRepository;
import com.scrumandcoke.movietheaterclub.service.BookingService;
import com.scrumandcoke.movietheaterclub.service.MovieService;
import com.scrumandcoke.movietheaterclub.service.ShowTimeService;
import com.scrumandcoke.movietheaterclub.service.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    BookingMapper bookingMapper;

    @Autowired
    ShowTimeService showTimeService;

    @Autowired
    MovieService movieService;

    @Autowired
    UserService userService;

    @Autowired
    EventBus eventBus;

    @Value("${booking.service.charge:1.5}")
    private Double serviceCharge;

    @Override
    public List<BookingDto> getAllBookings() {
        return bookingMapper.toDto(bookingRepository.findAll());
    }

    @Override
    public BookingDto getBookingById(Integer id) {
        return bookingMapper.toDto(bookingRepository.findById(id).get());
    }

    @Override
    @Transactional
    public void createBooking(BookingDto booking) throws GlobalException {
        try {
            validateBookingDetails(booking); //validation call
            verifySeatAllocation(booking.getShowtimeId(), booking.getSeatsBooked()); //validation call
            BookingEntity bookingEntity = bookingMapper.toEntity(booking);
            validateShowtime(booking.getShowtimeId());
            validateMovieReleaseDate(booking.getShowtimeId());
            reduceAvailableSeats(booking.getShowtimeId(), booking.getSeatsBooked());
            bookingEntity.setOnlineServiceFee(booking.getOnlineServiceFee());
            bookingEntity.setBookingDate(Date.from(Instant.now()));
            if (booking.getUserId() != null)
                processPayment(booking.getUserId(), booking.getPaymentMethod(), booking.getPointsAmount(),
                        booking.getCashAmount(), TransactionType.DEBIT);
            bookingEntity.setBookingStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(bookingEntity);
        } catch (Exception exception) {
            log.error("Error saving booking");
            throw new GlobalException(exception.getMessage(), exception);
        }
    }

    private void validateMovieReleaseDate(Integer showtimeId) throws GlobalException {
        Date releaseDate = movieService.getMovie(showTimeService.getShowTime(showtimeId).getMovieId()).getReleaseDate();
        Instant releaseInstant = releaseDate.toInstant();
        Instant currentInstant = Instant.now();

        if (releaseInstant.isAfter(currentInstant)) {
            Duration duration = Duration.between(currentInstant, releaseInstant);

            if (duration.toDays() > 7) {
                throw new GlobalException("Booking cannot be created more than 7 days in advance");
            }
        }
    }

    @Override
    @Transactional
    public void cancelBooking(Integer id) throws GlobalException {
        validateCancellationRequest(id); // New validation call
        BookingEntity bookingEntity = bookingRepository.findById(id).get();
        checkCancelEligibility(bookingEntity.getShowtime(), bookingEntity.getBookingStatus());
        bookingEntity.setBookingStatus(BookingStatus.CANCELLED);
        processPayment(bookingEntity.getUserId(), bookingEntity.getPaymentMethod(), bookingEntity.getPointsAmount(),
                bookingEntity.getCashAmount(), TransactionType.CREDIT);
        unblockSeats(bookingEntity.getShowtime().getId(), bookingEntity.getSeatsBooked());
        bookingRepository.save(bookingEntity);
    }

    @Override
    public List<BookingDto> getAllBookingsByUserId(String userId) {
        return bookingMapper.toDto(bookingRepository.findByUserId(userId));
    }

    @Override
    public List<BookingDto> getAllUpcomingBookingsByUserId(String id) {
        return bookingMapper.toDto(bookingRepository.findByUserIdAndShowtime_TimeAfterAndBookingStatus(
                id, Date.from(Instant.now()), BookingStatus.CONFIRMED));
    }

    @Override
    public List<BookingDto> getAllPastBookingsByUserId(String id) {
        return bookingMapper.toDto(bookingRepository.findByUserIdAndShowtime_TimeBeforeAndBookingStatus
                (id, Date.from(Instant.now()), BookingStatus.CONFIRMED));
    }

    @Override
    public List<BookingDto> getAllCancelledByUserId(String id) {
        return bookingMapper.toDto(bookingRepository.findByUserIdAndBookingStatus
                (id, BookingStatus.CANCELLED));
    }

    private void validateShowtime(Integer showTimeId) throws GlobalException {
        ShowTimeDto showtime = showTimeService.getShowTime(showTimeId);

        // Check if the showtime is in the future
        Date currentDateTime = Date.from(Instant.now());
        Date showtimeDateTime = showtime.getTime();
        if (showtimeDateTime.before(currentDateTime)) {
            throw new GlobalException("Showtime must be in the future");
        }
    }

    private void reduceAvailableSeats(Integer showtimeId, int seatsBooked) throws GlobalException {
        ShowTimeDto showtime = showTimeService.getShowTime(showtimeId);
        int availableSeats = showtime.getAvailableSeats();

        if (seatsBooked > 8 || seatsBooked < 1)
            throw new GlobalException("Number of seats should be between 1 to 8");

        if (seatsBooked > availableSeats) {
            throw new GlobalException("Not enough available seats for the booking");
        }

        // Reduce available seats
        showtime.setAvailableSeats(availableSeats - seatsBooked);

        // Save the updated Showtime entity
        showTimeService.updateShowTime(showtimeId, showtime);
    }

    private void processPayment(String userId, PaymentMethod paymentMethod, Double pointsAmount,
                                Double cashPoints, TransactionType transactionType) throws GlobalException {
        if (paymentMethod.equals(PaymentMethod.POINTS)) {
            if (transactionType == TransactionType.DEBIT) {
                validatePointsBalance(userId, pointsAmount);
            }
            userService.updatePoints(userId, pointsAmount, transactionType);
        }
        if (transactionType == TransactionType.DEBIT)
            userService.updatePoints(userId, Math.ceil(cashPoints), TransactionType.CREDIT);
        else
            userService.updatePoints(userId, Math.ceil(cashPoints), TransactionType.DEBIT);
    }

    private void validatePointsBalance(String userId, Double pointsAmount) throws GlobalException {
        Double balance = userService.getUserByUserId(userId).getPoints();
        if (balance < pointsAmount) {
            throw new GlobalException("Not enough balance");
        }
    }

    private void checkCancelEligibility(ShowTimeEntity showtime, BookingStatus bookingStatus) throws GlobalException {
        if (bookingStatus.equals(BookingStatus.CANCELLED))
            throw new GlobalException("Booking already cancelled");

        // Checking if current datetime is past showtime
        if (Date.from(Instant.now()).after(showtime.getTime()))
            throw new GlobalException("Booking not eligible for cancellation");
    }

    private void unblockSeats(Integer id, Integer seatsBooked) throws GlobalException {
        ShowTimeDto showtime = showTimeService.getShowTime(id);

        // Unblock available seats
        showtime.setAvailableSeats(showtime.getAvailableSeats() + seatsBooked);

        // Save the updated Showtime entity
        showTimeService.updateShowTime(id, showtime);
    }

    //Validations
    private void validateBookingDetails(BookingDto booking) throws GlobalException {
        if (booking == null) {
            throw new GlobalException("Booking details cannot be null");
        }

        int seatsBooked = booking.getSeatsBooked();
        if (seatsBooked < 1 || seatsBooked > 8) {
            throw new GlobalException("Number of seats should be between 1 to 8");
        }

        PaymentMethod paymentMethod = booking.getPaymentMethod();
        if (paymentMethod == null) {
            throw new GlobalException("Payment method cannot be null");
        }

        if (paymentMethod == PaymentMethod.POINTS && booking.getUserId() == null) {
            throw new GlobalException("Non-logged users cannot pay with points");
        }
    }

    private void validateCancellationRequest(Integer bookingId) throws GlobalException {
        if (bookingId == null || bookingId < 1) {
            throw new GlobalException("Invalid booking ID");
        }

        Optional<BookingEntity> bookingEntityOptional = bookingRepository.findById(bookingId);
        if (!bookingEntityOptional.isPresent()) {
            throw new GlobalException("Booking with the specified ID does not exist");
        }

        BookingEntity bookingEntity = bookingEntityOptional.get();

        // Check if the booking is already cancelled
        if (bookingEntity.getBookingStatus().equals(BookingStatus.CANCELLED)) {
            throw new GlobalException("Booking is already cancelled");
        }

        // Check if the cancellation is requested within the permissible time frame
        if (!isCancellationRequestInPermissibleTimeFrame(bookingEntity.getShowtime())) {
            throw new GlobalException("Cancellation request is outside the permissible time frame");
        }
    }

    private boolean isCancellationRequestInPermissibleTimeFrame(ShowTimeEntity showTime) {
        // Implement the logic to check if the cancellation request is within the permissible time frame
        Date now = Date.from(Instant.now());
        long timeDifference = showTime.getTime().getTime() - now.getTime();
        return timeDifference > TimeUnit.HOURS.toMillis(2); // replace '2' with your specific time frame
    }

    private void verifySeatAllocation(Integer showtimeId, int seatsToBook) throws GlobalException {
        if (showtimeId == null) {
            throw new GlobalException("Invalid showtime ID");
        }

        ShowTimeDto showtime = showTimeService.getShowTime(showtimeId);
        if (showtime == null) {
            throw new GlobalException("Showtime does not exist");
        }

        int availableSeats = showtime.getAvailableSeats();
        if (seatsToBook > availableSeats) {
            throw new GlobalException("Not enough available seats for the booking");
        }
    }

}
