<p align="center">
  <a href="https://github.com/palexdev/MaterialFX">
    <img src="https://github.com/user-attachments/assets/842c8633-ef07-484e-bb88-468b1edf57b9" alt="Logo">
  </a>
</p>

## Table of contents

* [Overview](#overview)
* [Getting started](#getting-started)
* [Screens and features](#screens-and-features)
  * [Register and Log in](#register-and-log-in)
  * [Home screen](#home-screen)
  * [Top bar](#top-bar)
    * [Navigate](#navigate)
    * [Notifications](#notifications)
    * [User Menu](#user-menu)
    * [Search bar](#search-bar)
  * [Book detail](#book-detail)
  * [Book shelf](#book-shelf)
  * [Categories](#categories)
  * [Advanced search](#advandced-search)
  * [Add a new book](#add-a-new-book)
  * [Manage](#manage)

## Overview
libify is a library app that simulates a real library. You can search for books, borrow them, and store them in your bookshelf. You can also post your own books here and share them with others.

## Getting started
To use the app, you need to:
* Download or clone source code from this github.
* Install jdk21 or later. (jdk23 recommended).
* Install mysql. Download and add MySQL Connector/J to the project.
* Create a database (you can export sql code from: https://dbdiagram.io/d/OOP-6704a091fb079c7ebdabcbcb).
* Install required dependencies and run.

## Screens and features

### Register and Log in

This screen will appear when you open the app. Here you can login with username and password.

![image](https://github.com/user-attachments/assets/abeac50a-ff36-4001-ac31-e6d89d46c844)

If you haven't have an account, go to register screen by click on "register" on the right-bottom.

![image](https://github.com/user-attachments/assets/bcc9d1e6-b80e-4528-971f-84df944fa4ed)

### Home screen

After logged in, you will see the home screen. 

![image](https://github.com/user-attachments/assets/7d9fe1ba-27a8-433f-869c-f738652d0612)

### Top bar

![image](https://github.com/user-attachments/assets/ef99012f-89ef-4b21-9e2b-de654fbec62b)

The top bar will appear on almost every screen, display important information, and allow you to navigate between screens.

#### Navigate

![image](https://github.com/user-attachments/assets/00ab9b13-55f3-4049-b3d8-7d83d2d201f8)

Navigate between main screens: Home, Categories and Advanced search.

#### Notifications

![image](https://github.com/user-attachments/assets/2f359a82-c560-46c5-aeb9-bb3c3ac7ab3c)

Show notifications when a book you borrowed is overdue or when a book on your wishlist becomes available.

#### User Menu

<div style="display: flex; justify-content: space-around;">
  <img src="https://github.com/user-attachments/assets/f07ca408-89ef-4f05-a56f-4403473771e7" alt="Image 1" style="width: 30%;">
  <img src="https://github.com/user-attachments/assets/cbd0f03b-2850-4e65-ae29-797ca30c1272" alt="Image 2" style="width: 30%;">
  <img src="https://github.com/user-attachments/assets/772f510c-8f7c-490c-8e03-f5600ade0392" alt="Image 3" style="width: 30%;">
</div>


If you are an moderator, there will be addition features [Add-a-new-book](#add-a-new-book) and [My-books](#my-books) (books that your added to the library). If you are an admin, you can access [Manage](#manage).

#### Search bar

![image](https://github.com/user-attachments/assets/5bcb97f7-9a2b-43da-8cbf-5d7c1c76fcea)

A search bar allows you to quickly search for books by their titles.

