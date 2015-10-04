# ReconcileTravelExpenses
This is a personal project that allowed me to reconcile foreign transactions paid with a credit card with local (usd) bank transactions.

I intended to make the code and test files very easy to understand, but the main idea is the following:

- When you are travelling abroad, you should keep track of your expenses (YourExpensesFile).
- Sometimes, your expenses are in another currency, and you have an estimate, but the bank might register it with another amount.
- For example: you might pay for dinner in Paris for 10 euros, and the exchange rate you have is 1.12, but the bank charges you on your credit card 10.15 (using 1.15).
- Sometimes the differences are a bit more, or there might be a few expenses that are similar, which might become overwhelming to reconcile. 
- If you download your card statement (BankFile) you can use this app to use heuristics to try to match your travel expenses with the bank. 
It will also report which transactions didn't reconcile or show several options if there are multiple options.
