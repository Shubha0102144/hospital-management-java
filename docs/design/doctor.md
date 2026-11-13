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
