## Feature List

Common Features for All Users
Home/Landing Page: Homepage designed for a quick preview.
Features include:
- View Currently Playing Movies: Displaying currently showing movies with poster,          name and genre.
- View Upcoming Movies: List of soon-to-be-released movies.
- View All Theaters: List of theaters that include details about the theater.
- View All Locations: Feature to view all theater locations.
- View All Showtimes: Users can see showtimes for different movies across various theaters.
- View Membership Type: View type of membership whether it is Regular or Premium.
- Registration/Signup: Sign-in, sign-up and registration page.
- Ticket Booking: A booking system where users can,
    1. Select a movie.
    2. Choose a showtime.
    3. Pick seats.
    4. Complete the transaction. The system displays total cost, including any service fees (with waivers for members).
       Features for Members (Regular and Premium)

- View Member's Page: Dashboard displaying recent ticket purchases, reward points.
- Create Multiple Seat Booking: Booking functionality allowing members to book up to 8 seats in a single transaction.
- Add Rewards Points Accumulation: Members earn points for every purchase.
- Create service fee waiver for premium members: Booking fee automatically waived at checkout.
  Features for Theater Employees (Admins)

Movie/Showtime Management: System accessible only to theater employees. This system allows for the addition, updating, and removal of movie listings and showtimes.
Includes,
- Add movies: Employees can add new movies to the system with details like movie title, synopsis, duration, genre and release date.
- Schedule showtimes: Schedule showtimes for each movie, assigning them to specific screens at different theaters.
  -Add movies to theaters: Assigning movies to specific theaters within a location and seating capacity.
- Updating movies: Updating existing movies and showtimes.
- Removing movies: Delete existing movies and showtimes.
  -View movies: View list of movies.
  -Configure seating capacity: Theater employees can set the seating capacity for each screen within a theater.
- View analytics dashboard: Theater occupancy rates over (30/60/90 days) summarized by location and movies.
  -Configure discounts: Discount management system that allows theater employees to set and manage discounted prices for shows.
  1.Discount based on time: Setting discounted prices for shows before 6 pm.
  2.Discount based on day: Reduced ticket prices on Tuesdays.

Role-Based Authentication Control
RBAC based on three types of users: Regular, Premium and Guest.

Deployment
Deploying API and Database to AWS EC2 Cluster of 2 instances  + RDS. The EC2 instances are behind an application load balancer.