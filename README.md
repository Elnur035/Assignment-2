Database Connectivity (connectToDatabase, closeDBConnection):
It is about establishing and closing the connection to the PostgreSQL database using JDBC.

SQL Query Execution (executeSQLQuery):
Executes SQL queries based on the provided query string. Handles both data retrieval (SELECT) and manipulation (INSERT, UPDATE, DELETE).

Transaction Management (placeOrder, isStockAvailable, updateAvailableStock):
placeOrder: Handles order placement after checking book stock availability and updating stock.
isStockAvailable: Checks if a book's stock is available.
updateAvailableStock: Decreases the available stock count after a successful order.

Metadata Access (displayDatabaseInfo, displayTableInfo):
displayDatabaseInfo: Retrieves and displays database metadata (name, version).
displayTableInfo: Fetches and displays table columns, primary keys, and foreign keys.

Main Method Execution
Connects to the database, creates tables, populates them with sample data, and demonstrates functionalities: displaying database metadata, placing an order for a book.
