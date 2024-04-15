# Summary

## Comments

In general, the assignment was clear and provided good guidance for improving the file management API. 
Understanding the Acceptance Criteria wasn't too difficult, which made following the instructions straightforward.

To make the API endpoints more consistent and easier to understand, consider renaming them to use the plural form "files" for all file management-related endpoints (assigment.md).

## Which part of the assignment took the most time and why?
The part that took up most of my time was definitely writing tests. It was quite a task to make sure everything was working properly and handling all sorts of situations.

Also, when it comes to documentation, I usually use Swagger annotations in Controller. But this time, it turned out to be a bit tricky. I generated the OpenAPI YAML file directly using IntelliJ and some things were missing. Adding things manually was harder than I expected.

Oh, and here's a little mistake I made at the beginning: I thought it would be a good idea to directly deserialize the metadata JSON into a Map. But later on, I realized it was just unnecessary extra work.

## What You learned

From this assignment, I learned that in Spring Boot applications using MongoDB, transaction management requires a replica set configuration rather than a standalone setup. It became clear that in standalone configurations, transactional operations do not function as expected, hindering the rollback capability. This highlighted the necessity of ensuring the correct MongoDB setup to enable effective transaction handling in Spring Boot applications.

Additionally, I gained valuable experience in unit testing with Kotlin using MockK (previously used Mockito), which allowed me to ensure the reliability of my code.

## TODOs

- Implement file type validation for the file upload endpoint to ensure only valid file types are accepted.
- Extract the Content-Type and name directly from the original file instead of relying on form-data
- Limit upload file size
- Enable transaction management to roll back if error happens while uploading/deleting file.
- Enhance data security by implementing encryption for stored files, ensuring that sensitive information remains protected.
- Implement lifecycle management policies to automatically archive or delete files based on predefined criteria, such as age or usage frequency
