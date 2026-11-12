Doctor Fields:
full name,
specialty,
license number,
department

indexes and uniqueness rules:
- An unique id is generated for every doctor.
- Full name is required.
- Specialty is required 
- License number is required.
- Two doctors cannot share the same license number. 
- A refused attempt leaves the directory unchanged.
- A missing name, specialty, license number, or department, or a value that is too long, is refused, and the reason names the problem.

Relationship to Patient
- Patients currently store primaryDoctorName as free text(along with doctor id for reference).
- A doctor can view and update patient details.
- A doctor and an administrator can each open any patient chart.
- A doctor can see their own upcoming appointments.
-  A doctor can remove a chart with no upcoming appointment


API sketch
path:http://localhost:8081/api/v1/users
- post ->to register
- get ->to view pateint detials
- put ->to update patient chart
- delete ->to remove a chart


