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

## task5

# Remove a doctor

A doctor or an administrator needs to take a clinician out of the directory when that person should no longer be offered as a primary doctor.

## Information

The hospital needs to know which doctor is being removed.

## Who can do this

- Doctor and administrator: yes
- Patient: no
- Anyone who is not signed in: no

## Requirement

A doctor or an administrator can remove a doctor who is not the primary doctor on any patient chart. After removal, that doctor cannot be found.

## Rules

- The doctor must already exist. An unknown doctor is refused.
- Removal is refused while any patient chart still names that doctor as the primary doctor. Those charts stay on file.
- After a successful removal, a search and a direct view no longer find that doctor.
- A refused attempt leaves the doctor and the charts unchanged.

## API sketch

- path- /api/v1/doctors{id}
- method->Delete

200- doctor found
400 - validation failed
403- Is unauthorized
401 - Missing or invalid token

# task6

# tie-patients-to-doctors

Clinicians need a patient chart to name a real doctor from the directory, so a rename or a correction shows up on every chart that uses that doctor.

## Requirement

A chart names a doctor the directory already knows. When that doctor's name changes, every chart that names them shows the new name. Charts already on file remain readable.

## Information

Each chart remembers which directory doctor is the primary doctor, and shows that doctor's current name.

## Who may do this

This changes the rules for registering a chart and for updating a chart.

- Doctor and administrator: may register or update a chart so that it names a known doctor
- Patient: no
- Anyone who is not signed in: no

## Rules

- Registering a chart, or changing a chart, requires a primary doctor that can already be found in the directory.
- An unknown doctor is refused, and the chart is left unchanged. A new chart is not created.
- When a doctor's name is corrected, every chart that names that doctor shows the corrected name.
- Charts already on file before this capability exists stay available. Each of them still shows a primary doctor name a reader can recognize.
- Removing a doctor is still refused while any chart names that doctor.

# task7

# Register a patient chart

A doctor or an administrator needs to open a chart for a patient. Registering for an account creates a login only. A chart is a separate record.

## Requirement

A doctor or an administrator can open a chart for an existing patient account. The chart can be found later with the details that were saved.

## Information

- The patient account the chart belongs to (required)
- Full name (required)
- Primary doctor (required). Once doctors are part of the directory, the chart names a doctor the directory already knows.
- Medical record number (required)
- Date of birth (required)
- Gender (required): female, male, other, or unknown
- Phone (optional)
- Blood group (optional)
- Notes (optional)

## Who may do this

- Doctor and administrator: yes
- Patient: no
- Anyone who is not signed in: no

## Rules

- The chart belongs to a patient account that already exists. One patient account has at most one chart.
- Full name is required and is between 2 and 160 characters.
- Primary doctor is required. The displayed doctor name is between 1 and 160 characters.
- Medical record number is required and looks like `MRN-100001`: the letters MRN, a hyphen, and six digits. Two charts cannot share the same medical record number. Comparison ignores letter case and spaces.
- Date of birth is required and is not in the future.
- Gender is required and is one of female, male, other, or unknown.
- Phone, when present, is between 7 and 20 characters and uses digits, spaces, or the characters `+`, `(`, `)`, `.`, and `-`.
- Blood group, when present, is one of A+, A-, B+, B-, AB+, AB-, O+, or O-.
- Notes, when present, are at most 2,000 characters.
- A refused attempt does not open a chart.

## API sketch

- path- /api/v1/patients
- method->Post

200- patient added
400 - validation failed
403- Is unauthorized
401 - Missing or invalid token

# task8

# View a patient chart

A patient needs to read their own chart. A doctor or an administrator needs to open any chart.

## Requirement

A patient can open only the chart that belongs to their account. A doctor or an administrator can open any chart that is already on file.

## Information

The view shows the patient name, primary doctor, medical record number, date of birth, gender, phone, blood group, and notes. Empty optional fields are shown as empty.

## Who may do this

- Patient: yes, for their own chart only
- Doctor and administrator: yes, for any chart
- Anyone who is not signed in: no

## Rules

- The chart must already exist. An unknown chart is refused.
- A patient who opens a chart that belongs to someone else is refused.
- A patient with no chart is told that no chart was found.
- Opening a chart does not change it.

- path- /api/v1/patients/{id}
- method->get

200- patient found
400 - validation failed
403- Is unauthorized
401 - Missing or invalid token
404-patient not found
