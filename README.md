# Ivy-Lee Task Tracker
![Java CI with Gradle](https://github.com/ottx96/ivy-lee/workflows/Java%20CI%20with%20Gradle/badge.svg)
![Create Release / upload assets](https://github.com/ottx96/ivy-lee/workflows/Create%20Release%20/%20upload%20assets/badge.svg)  
**_Track your tasks with the ivy-lee method._**  
_See [https://jamesclear.com/ivy-lee](https://jamesclear.com/ivy-lee)_

## Features

### Markdown-Syntax

Task descriptions support _**markdown**_.  
_See [https://www.markdownguide.org/basic-syntax/](https://www.markdownguide.org/basic-syntax/)_

#### Examples



##### Headers

***
```markdown
# h1
## h2
### h3
#### h4
##### h5
###### h6
```
_Renders to:_  
![custom styles](documentation/files/headers.png)
***

##### Bold / Italic

***
```markdown
**Bold**  
_Italic_  
_**Bold Italic**_  
```
_Renders to:_  
![custom styles](documentation/files/bold-italic.png)
***

##### Listings / Enumerations

***
```markdown
1. first item
	- sub-item 1
2. second item
	- sub-item 2
	- sub-item 3
3. thrid item
---
- the list goes on..
- ..and on..
```
_Renders to:_  
![custom styles](documentation/files/listings.png)
***

##### Checkboxes / TODOs

***
```markdown
- [ ] first item  
- [x] second item  
- [ ] third item  
- [x] fourth item
```
_Renders to:_  
![custom styles](documentation/files/checkboxes.png)
***

##### Custom Styles
There are custom styles defined in [**style.css**](src/main/resources/de/ott/ivy/css/style.css).
  
***
```markdown
! This is an Information
!! Warning!
!v Success!
!x ERROR!
```
_Renders to:_
![custom styles](documentation/files/custom-syntax.png)
***

### Google Drive Support
###### (under construction)

### Share Tasks among devices
###### (under construction)

##  Extensions (Extension API)
Want to write your own extension(s) ?   
Visit the [**_Extension API Documentation_**](documentation/EXTENSION_API.md).
