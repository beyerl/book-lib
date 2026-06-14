L-Space is a local only Book Library and Reading Process App.

# Tech Stack
- Kotlin Android App
- Github Actions for Build Process 
- Material UI
- Persistent Db Storage on Phone

# Theming

- Sepia based Colour Scheme
- Use a Orang Utan Librarian from Terry Pratchetts Discworld Stories as App Icon and in selected Places in the App

# Views
## Home View
- Current Progress towards yearly reading goal
- Books curently reading in a carousel

## Search View
Search for a book and add it to your collection on one of the shelves mentioned below. Search should be based on common open source book libraries as used by https://github.com/bookwyrm-social

Found books are shown in a result list similar to the Library view and can be opened up in Search Detail View, similar to Book view. Result list aswell as Book view in this case have a plus button for adding the book to one of the shelves.

If the search is empty, a book can be created from scratch in new book view, similar to edit mode of book view. It can then be added to the library.


## Library View
The Library has different shelves. These can be used as filters to view books on one ore more shelves. The following shelves are sopporte:
- Reading list
- Now Reading
- Finished Reading
- Stopped Reading

The books on the currently selected shelves will be shown in a paged list. List size is configureable. The paged list includes a short book info containing: Thumbnail, Title, Author, Year published, Description incipit, Rating.

## Book View
Detailed View of the book containing the most relevant the Information available via API. Look at Book Wyrm for inspiration regarding information to be displayed. Book View provides an option to Rate the Book from 1 to 5 Stars. Ratings and information can be edited by switching to edit mode. Also show from when till when the book was read (moved from the Reading List to Now Reading and Now Reading to Finished Reading shelves).

## Achievements

Prompt the uset to select a number of books to be read this year at the end of the previous year. Track the progress of the books put from the "now reading" to the "Finished Reading" shelves in a progress bar. Show a list of "The year <yyyy> in Books"-summary entries. Clicking on an entry opens a list of books read in the particular year.

## Export/Import View
Export the Library in a goodreads compatible csv format and alternatively as a mardown file
Import library via Goodreads compatible csv files