# ProFynd

ProFynd is a mobile application designed to connect students with nearby tutors based on location, subject expertise, ratings, and availability. Developed as a multidisciplinary project by second-year preparatory class students at École Supérieure en Informatique 8 Mai 1945 (ESI-SBA), Sidi Bel-Abbès, Algeria, the app aims to streamline the tutor-student matchmaking process, enhancing academic support through personalized recommendations and efficient communication.

## Overview

The application leverages GPS technology to identify a student's location and suggest suitable tutors in the vicinity. Students can filter tutors by subject, education level, teaching style, and ratings from previous users. Tutors, in turn, can post announcements about their services, manage their profiles, and engage with potential students. The goal is to foster a reliable, user-friendly platform that promotes high-quality tutoring and academic progress.

This repository contains the source code for ProFynd version 1.1, implemented for Android devices.

## Features

- **User Registration and Profiles**: Simple registration process to create student or tutor profiles, including personal details, interests, qualifications, and location.
- **Tutor Search and Filtering**: Search for tutors based on proximity, subjects, experience, ratings, and teaching style.
- **Announcements and Engagements**: Tutors can post announcements (e.g., course availability, promotions). Students can like, comment, or engage with them.
- **Rating System**: Students rate tutors post-session, influencing recommendations and visibility.
- **Private Messaging**: Direct chat between students and tutors for personalized discussions.
- **Geo-Location Integration**: Uses device GPS to recommend nearby tutors and filter results.
- **Personalized Recommendations**: Algorithm-based suggestions tailored to user preferences, interactions, and location.
- **Profile Management**: Edit profiles, update schedules, and manage notifications.
- **Notifications**: Real-time alerts for new messages, engagements, or announcements.

## Technologies Used

- **Frontend**: Android Studio (Java)
- **Backend**: Firebase (for authentication, database, and real-time features)
- **Mapping**: Google Maps API (for geo-location and proximity-based searches)
- **Design Tools**: Figma (for UI/UX prototypes)
- **Development Tools**: Android SDK, Git for version control
- **Communication Tools**: Discord, Trello (for team collaboration during development)

## Installation

1. **Prerequisites**:
   - Android Studio (version 4.0 or higher)
   - Java Development Kit (JDK 8 or higher)
   - Firebase account (for backend setup)
   - Google Maps API key

2. **Clone the Repository**:
   ```
   git clone https://github.com/qmrx87/ProFynd-1.1.git
   ```

3. **Set Up Firebase**:
   - Create a Firebase project at [console.firebase.google.com](https://console.firebase.google.com).
   - Download the `google-services.json` file and place it in the `app/` directory.
   - Configure Firebase Authentication, Realtime Database, and Storage as per the app's requirements.

4. **Configure Google Maps API**:
   - Obtain an API key from the [Google Cloud Console](https://console.cloud.google.com).
   - Add the key to your `AndroidManifest.xml` file.

5. **Build and Run**:
   - Open the project in Android Studio.
   - Sync Gradle dependencies.
   - Build and run the app on an emulator or physical Android device (API level 21 or higher).

## Usage

1. **Launch the App**: Open ProFynd on your Android device.
2. **Register**: Create an account as a student or tutor, providing necessary details.
3. **For Students**:
   - Search for tutors using filters.
   - View profiles, ratings, and announcements.
   - Send messages or engage with posts.
4. **For Tutors**:
   - Add formations/announcements.
   - Manage your schedule and respond to inquiries.
5. **Explore Features**: Use the home screen to browse recommendations, check notifications, and update your profile.

## Project Structure

- `app/src/main/java/`: Core Java source code for activities, models, and utilities.
- `app/src/main/res/`: Resources including layouts, drawables, and strings.
- Diagrams and sequence flows (as documented in the project report) illustrate the app's architecture, including class diagrams for users, authentication, and searching.

## Contributors

This project was developed as part of the 2CPI Pluridisciplinary Project at ESI-SBA during the 2022-2023 academic year.

- **Team Members**:
  - Dait Dehane Yacine
  - Yahiaoui Yamina
  - Chaala Inas
  - Djili Maroua
  - Abdelkader Berrahal Said

- **Mentor**: Mr. Chaib

## Acknowledgments

We extend our gratitude to our mentor, Mr. Chaib, for his guidance and support. Special thanks to our families and peers for their encouragement throughout the project.
