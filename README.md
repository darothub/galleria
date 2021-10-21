## Assignment Task

### Problem statement
- User wants to be able to search for images
- User wants to be able to see images from search
- User wants continuous display of images as he/she scrolls
- User wants see previously searched images when there is not network connection

### Solution
- Integrate Shutterstock API for image search
- Integrate android paging 3.0 library for infinite scrolling 
- Search data cache

### Features
- Search
- Display with image and description
- Local caching
- Smooth scrolling
- Simple UI for great UX
- Security
- Testing

### Limitation
- While search works well, its behaviour is yet to be fully tested
- Continuous search request leads to `Too many requests` error from Shutterstock API

### Design Architecture
- MVVM

### External Libraries
- Jetpack libraries
- Retrofit
- OkHttp
- Sdp-android
- Coil
- Material design

<img src="https://user-images.githubusercontent.com/21008156/138369448-ff8da4b3-fd3a-4485-8d75-05096461b3ca.png" width="300" />

##### NB: Extensively, for a growing project, modularisation by feature would be the best approach
