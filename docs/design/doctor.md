## task1

## Add a doctor with fields

- Full name (required)
- Specialty (required)
- License number (required)
- Department (required)

## Requirement

The hospital keeps a doctor and can find that same doctor later.

## validation Rules

- Full name is required. After trimming extra spaces, it is between 2 and 160 characters.
- Specialty is required and is between 1 and 80 characters.
- License number is required and is between 1 and 40 characters.
- Two doctors cannot share the same license number. Comparison ignores letter case and extra spaces.
- Department is required and is between 1 and 80 characters.

## uniqueness rules

- Licence number should be unique for every doctor
- A second doctor with the same license number is refused, and the first record is unchanged.
- unique ID is generated for reference after registering the doctor

## API sketch

- path- /api/v1/doctors

200- doctor registered
400- Validation failed
401- Missing or invalid token
404- Doctor not found
409- Licence number conflict

## task2

## view a doctor by id

A signed-in person needs to open one doctor and read the directory record.

## Information

The view shows doctor's :

- Full name
- Specialty
- License number
- Department

## Who can do this

A signed-in person

- Admin
- Doctor
- Patient

## Requirement

Anyone who is signed in can open a single doctor that is already on file and read the directory record.

## Information

The view shows the full name, specialty, license number, and department saved for that doctor.

## Rules

- The doctor must already exist.
- An unknown doctor is refused, and the reason says the doctor was not found.
- Opening a doctor does not change the record.

## API sketch

- path- /api/v1/doctors{id}

200- doctor found
403- Is unauthorized
404- Doctor not found

## task3

# Search doctors by name

A signed-in person needs to find doctors without already knowing which record to open.

## Information

succesful search returns doctor's:

- Full name
- Specialty
- License number
- Department

## Who can do this

A signed-in person

- Admin
- Doctor
- Patient

## Requirement

Anyone who is signed in can page through doctors and narrow the list by name.

## Rules

- Each entry in the list shows the same details as opening that doctor: full name, specialty, license number, and department.
- When nothing matches, the result is an empty page.
- Results come back one page at a time. The reader can ask for a later page.
- A page holds at most 20 doctors.
- With no name filter, the page lists doctors in name order.

## API sketch

- path- /api/v1/doctors/search?name

200- doctor found
403- Is unauthorized

## task4

# Update a doctor

A doctor or an administrator needs to correct a clinician's name, specialty, license number, or department after the record was first saved.

## Information

All four information are required:

- full name
- specialty
- license number
- department.

## Who can do this

- Doctor and administrator: yes
- Patient: no
- Anyone who is not signed in: no

## Requirement

A doctor or an administrator can replace the saved details of one doctor. The updated doctor can still be found later.

## Rules

- The doctor must already exist. An unknown doctor is refused.
- Full name, specialty, license number, and department follow the same limits as when a doctor is added.
- The license number stays unique regardless of letter case and extra spaces.
- The doctor may keep their own current license number.
- A refused attempt leaves that doctor unchanged.

## API sketch

- path- /api/v1/doctors{id}

200- doctor found
400 - validation failed
403- Is unauthorized
401 - Missing or invalid token
409 - licence number conflict
