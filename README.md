# Fire Auth Kit

### Customize Designs

<table>
     <tr>
        <td>
          <img src="medias/img_ (1).jpg" width="200" height="400" />
        </td>
        <td>
          <img src="medias/img_ (3).jpg" width="200" height="400" />
        </td>   
     </tr>
     <tr>
        <td>
          <img src="medias/img_ (7).jpg" width="200" height="400" />
        </td>
        <td>
          <img src="medias/img_ (6).jpg" width="200" height="400" />
        </td>  
         <td>
          <img src="medias/img_ (5).jpg" width="200" height="400" />
        </td>  
         <td>
          <img src="medias/img_ (4).jpg" width="200" height="400" />
        </td>  
     </tr>
 </table>

# Step 1->
####    Make a Firebase Project
####    Register Your app, Provide SHA-1,  Signatures
# Step 2->
###    In Authentication Allow From Following as only those are supported by this Module
####        *) Email/Password    **NOTE** This method is mandatory 
####        1) Google           
####        2) Phone
####        3) MICROSOFT
####        4) YAHOO
####        5) GitHub
####        6) Twitter
###    *********NOTE**********
####           1) Its is important to note that authentication with Google also require to setup your project
###             with Google Cloud and provide specific info where require 
###            Same goes for Facebook and other Platforms that are supported
####           2) Some Methods require your App to be published on Google PlayStore before working 
###            e.g: Phone Authentication -- to use it for testing you can add numbers in your Firebase test numbers

# Step 3->
####    DownLoad config file for your app and Add it in to you App
# Step 4->
####    Make some changes in you 'build gradle' file Project Level
####    add following dependencies Version can be latest

        dependencies {
            // Add this
            classpath 'com.google.gms:google-services:4.3.15'
            ....
        }
#
        allprojects {
            repositories {
                google()
                mavenCentral()
                maven { url "https://jitpack.io" }
                ....
            }
        }

# Step 5->
####    Now Make some changes in you 'build gradle' file App module Level
####    add following dependencies Version can be latest

        plugins {
            id 'com.android.application'
            id 'com.google.gms.google-services'
            ....
        }
        ......
#
        dependencies {
            implementation 'com.github.Wasi-Ibn-Adam:FireAuthKit:1.1.1'
            .....
        }
# Step 6->
####    This was all setup to include module in your Project
####    Now lets use it in the project 
####        As Login/Signup required to be first in app this Module is designed in a way to use it perfectly
####        also no need for extra Splash_Activity/Screen for your app. this module covers all
    
#####   1 -> Create an activity and extend it with 'SSO_Activity'   
#####   2 -> mention it in your manifest file   // just like normal activity
#####   3 -> implement the function 'Runner setRunner()'
#####   4 -> override functions of Runner class to change settings // details are provided in each function description

####   and there you go nothing to write and worry about how things will work in it
####   you can use this module in projects as many as you wish with one Firebase account so that your 
####   all app users can be at one place 
####
####   to access information of user you can use 'SSO.class'
####   like: name, email, phone, image-uri, and user-id(UID)
####   moreover if you have dependencies of firebase auth you can also get user from there directly
####   build.gradle/app
    dependencies {
        implementation 'com.google.firebase:firebase-auth:X.X.X'
        -------
    }
## JAVA CODE
####      FirebaseUser user = SSO.getUser();
###               or
####      FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
