<p align="center">
  <a href="https://github.com/hainm112123/OOP---Library-Manager">
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
  * [Profile](#profile)
  * [Change password](#change-password)
  * [Book's detail](#books-detail)
  * [Borrow requests](#borrow-requests)
  * [Book shelf](#book-shelf)
  * [Rating](#rating)
  * [Categories](#categories)
  * [Advanced search](#advanced-search)
  * [Manage your books](#manage-your-books)
    * [Add a new book](#add-a-new-book)
    * [Edit books](#edit-books)
  * [Recommendation system](#recommendation-system)
  * [Manage app](#manage-app)

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

This screen will appear when you open the app. Here you can login with email and password, or with your google accounts.

![image](https://github.com/user-attachments/assets/79758868-9ddf-4566-804d-752f901b0ff9)

If you haven't have an account, go to register screen by click on "register" on the right-bottom.

![image](https://github.com/user-attachments/assets/ee005e3e-dcb3-42b6-8f87-989b934adda9)

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

![image](https://github.com/user-attachments/assets/390cd6db-4f79-4d5a-aba4-ab5d29ef0df7)

Show notifications about newest informations such as when a book you borrowed is overdue, when a book on your wishlist becomes available or when your borrow request is approved/declined.

#### User Menu

<div style="display: flex; justify-content: space-around;">
  <img src="https://github.com/user-attachments/assets/f07ca408-89ef-4f05-a56f-4403473771e7" alt="Image 1" style="width: 30%;">
  <img src="https://github.com/user-attachments/assets/9f894b77-704c-4233-be05-8bccb648188d" alt="Image 2" style="width: 30%;">
  <img src="https://github.com/user-attachments/assets/6bcecf73-3478-4d74-8b10-cfefc3e5aa43" alt="Image 3" style="width: 30%;">
</div>

From this menu, you can:
* Access your [profile](#profile)
* [Change password](#change-password)
* Access your [book shelf](#book-shelf)
If you are an moderator, there will be addition features [Add a new book](#add-a-new-book), [My books](#my-books) (books that your added to the library) and approve/decline users' borrow requests. If you are an admin, you can access [Manage](#manage-app).

#### Search bar

![image](https://github.com/user-attachments/assets/5bcb97f7-9a2b-43da-8cbf-5d7c1c76fcea)

A search bar allows you to quickly search for books by their titles.

### Profile

![image](https://github.com/user-attachments/assets/0a682a43-8453-4f29-84ed-0099f60b1978)

In this screen, you will see your informations, avatar, a recap of your books borrowing. You can also change your avatar in this screen.

### Change password

![image](https://github.com/user-attachments/assets/3c12d01c-5293-48d2-9d21-bd8af99bbc91)

### Book's detail

![image](https://github.com/user-attachments/assets/090b8f43-1c0b-4ddd-9078-9df3a319c9b6)

By clicked on a book, libify will navigate to book detail screen. Here, informations such as title, author, description, rating, review, etc will be displayed. From this screen, you can borrow/return this book or add it to your wishlist if it is currently unavailable. You can also see other users' reviews at the bottom.

After you successfully borrow a book or add it to your wishlist, it will appear in your [book shelf](#book-shelf). Note that, you will need admin or moderator to approve your [borrow requests](#borrow-requests).
After you return a book, libify will navigate to [rating screen](#rating).

![image](https://github.com/user-attachments/assets/fc2172f3-d889-4662-af04-d85bdca09f8a)

If you are the book's owner, you will allow to [edit](#edit-books) it.

### Borrow requests

![image](https://github.com/user-attachments/assets/3fe599cd-ed44-44b1-9127-6f85e508fea9)

If you are a moderator or an admin, you can approve/decline users' borrow requests.

### Book shelf

![image](https://github.com/user-attachments/assets/91ada9bc-aab4-4127-8edc-0d1436ceb870)

Store books that you are reading/borrowing, have completed reading, and books that are in your wishlist.

### Rating

![image](https://github.com/user-attachments/assets/75cca33d-5ecc-46e1-a9da-944ef36af657)

After you return a book, the app will navigate to this screen. Here you can rate and write a review to share you feeling about this book with other users.

![image](https://github.com/user-attachments/assets/8b6145d8-4783-4aee-b9b0-1b27e3e4ff64)

Your reviews will apear on the bottom of in [book's detail screen](#books-detail)

### Categories

![image](https://github.com/user-attachments/assets/e5f28b14-add3-40bb-a96e-d7aa4f36986e)

![image](https://github.com/user-attachments/assets/a364ad79-6007-4348-8b84-e3e74c3c35a0)

libify provides a lot of categories to classify books. Through the categories button in the top bar, you can access books with a specific category.

### Advanced search

![image](https://github.com/user-attachments/assets/5ed20801-4036-477e-9f59-dbf982b90962)

A powerful search engine that allows you to use variety of sorting options and filters.

### Manage your books

#### Add a new book

![image](https://github.com/user-attachments/assets/654b2cdb-abb3-4426-b63f-2b277fc28e88)

If you are a moderator or an admin, you are allowed to add a new book to library. You can access this screen through [user menu](#user-menu). There are 2 ways to add new a book: manually fill the form above,

![image](https://github.com/user-attachments/assets/321016b8-8954-45ff-876f-93eddb801455) 

or search books by title, authors, subject, isbn, etc., using Google Books APIs, which will automically fill out the form when you select a book.

#### Edit books

![image](https://github.com/user-attachments/assets/3786342f-e832-41b1-9785-86a26b294082)

You are allowed to edit a book if you are its owner. After access this screen through [book's detail](#books-detail) screen, you can edit the book's informations or remove it from library.

### Recommendation system

![image](https://github.com/user-attachments/assets/3dc5cd40-3027-4b2d-b7cf-014addf3d6a5)

New books, most popular books, high rated books and books that user may like (based on users' activities) will be displayed in home screen.

### Manage app

![image](https://github.com/user-attachments/assets/13791afc-826e-4e2a-8000-062ec1984c5b)
![image](https://github.com/user-attachments/assets/541a1ece-48f7-48e2-b68c-04b567447e0d)

If you are an admin, you are allowed to access this screen through [user menu](#user-menu). In this screen, you can view app data and allow to edit some of it.
