# ExameVirtualWallet
It is an exam where it was necessary to emulate a virtual wallet

**This project was developed to showcase my skills in creating and organizing an app from the ground up**

It was created using Kotlin with Spring boot, providing an api with the following features

## APIs
### POST /reset
Clear all accounts existing on the wallet

### POST /event
Send a event action to wallet, currently is supported 3 actions (DEPOSIT, WITHDRAW and TRANSFER)

### GET /balace
With this endpoint you can get the current balance for accounts generate by events DEPOSIT or TRANSFER.
