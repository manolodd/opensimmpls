# PROJECT STATUS (Java 8)

- Master branch [![Build Status](https://api.travis-ci.org/manolodd/opensimmpls.svg?branch=master)](https://travis-ci.org/manolodd/opensimmpls)

- Development branch [![Build Status](https://api.travis-ci.org/manolodd/opensimmpls.svg?branch=development)](https://travis-ci.org/manolodd/opensimmpls?branch=development)

# THE PROJECT

<b>OpenSimMPLS</b> is an MPLS network simulator, multiplatform and mutilanguage. It is easy-to-use and is intended for teaching activities. It can be used as well to test new protocols, techniques and methods related to MPLS and/or GoS. OpenSimMPLS is a mature project used in universities and companies in more tan 130 different countries to teach, research and design MPLS networks. It was first developed in 2004 as part of a research project at the University of Extremadura. It was hosted since then in SourceForge.net as a CVS project and since 2014 it was updated as a GIT repository and hosted in GitHub forge.

It supports standard MPLS operation as well as GoS/MPLS operation. See "Guarantee of Service (GoS) Support Over MPLS using Active Techniques" proposal at http://opensimmpls.manolodominguez.com/content/common/pdf/documentation/gossobrempls.pdf

![OpenSimMPLS logo](https://github.com/manolodd/opensimmpls/raw/master/src/main/resources/com/manolodominguez/opensimmpls/resources/images/splash_inicio.png)

# LICENSE

## This release:
 
- <b>OpenSimMPLS 2.3</b> is released under the terms of Apache Software License 2.0.

## Previous releases:

- <b>OpenSimMPLS 2.2</b> was released under the terms of Apache Software License 2.0.
- <b>OpenSimMPLS 2.1</b> was released under the terms of Apache Software License 2.0.
- <b>OpenSimMPLS 2.0</b> was released under the terms of Apache Software License 2.0.
- <b>OpenSimMPLS 1.1</b> was released under the terms of GPLv3.0+.
- <b>OpenSimMPLS 1.0</b> was released under the terms of GPLv2.0+.

# PEOPLE BEHIND OPENSIMMPLS

## Author:
    
 - Manuel Domínguez-Dorado - <ingeniero@ManoloDominguez.com>
   
## Other collaborators (only for release 1.0):

 - Javier Carmona Murillo - <jcarmur@unex.es>
    
    
Please, refer always to the project home page at:

 - http://opensimmpls.manolodominguez.com/

# COMPILING FROM SOURCES

The best option is to download latest compiled stable releases from the releases section of this repository. However, if you want to test new features (please, do it and give feedback), you will need to compile the project from sources. Follow these steps:

 - Clone the OpenSimMPLS repo: 
```console
git clone https://github.com/manolodd/opensimmpls.git
```
 - Compile the code and obtain a binary jar including all you need (you will need to install Maven before):
```console
cd opensimmpls
mvn package
```
 - The jar file will be located in "target" directory.
```console
cd target
```
- Now, run the simulator:
```console
java -jar openSimMPLS-{YourVersion}-with-dependencies.jar
```
- Need some scenarios to try? There is not problem!! You'll find them at the examples folder (root of your cloned repository). You can also download these examples, for your OpenSimMPLS version, in the Releases section of this repository.


# How to use OpenSimMPLS

You can open a quick start guide directly from the simulator GUI. However, you can find the same guide in some languages at https://github.com/manolodd/opensimmpls/tree/master/src/main/resources/com/manolodominguez/opensimmpls/resources/guides


# THIRD-PARTY COMPONENTS

OpenSimMPLS uses third-party components each one of them having its own OSS license. License compatibility has been taken into account to allow OpenSimMPLS be released under its current OSS licence. They are:

- Jfreechart 1.5.0 - LGPL - http://www.jfree.org/jfreechart
- slf4j-api 2.0.0-alpha1 - MIT - https://www.slf4j.org
- slf4j-simple 2.0.0-alpha1 - MIT - https://www.slf4j.org
- miglayout-swing 5.2 - BSD - https://github.com/mikaelgrev/miglayout
- miglayout-core 5.2 - BSD - https://github.com/mikaelgrev/miglayout
- AbsoluteLayout RELEASE90 - CDDL (Being replaced in OpenSimMPLS by miglayout)

Thanks folks!


# HOW TO CONTRIBUTE

OpenSimMPLS is opensource software. I encourage you to modify it as much as possible; but I would like you to send this modifications back and, hence, became an OpenSimMPLS contributor. In this way, all the people will benefit from them as you are doing downloading and using OpenSimMPLS now.

If you want to contribute to OpenSimMPLS project, follow these instructions:

 - Log in to your GitHub account.
 - Look for OpenSimMPLS project.
 - Select the <b>development branch</b> of OpenSimMPLS and create a fork in your own GitHub repository.
 - Clone <b>your</b> OpenSimMPLS repository to your PC or laptop.
 - Do all modifications in local, file additions or deletions, modifications, commits...
 - Push your modifications to <b>your</b> remote github OpenSimMPLS repository.
 - Go again to yout GitHub account, choose your OpenSimMPLS repository and click on the tab "Pull requests".
 - Then, click on the green button at the right "New pull request". This will guide you to make a pull request (send your modifications on your own OpenSimMPLS repository to OpenSimMPLS main repository from where you did your fork at the beginning). 
 - Choose the development branch of manolodd/OpenSimMPLS as base branch to merge to. Then "Create pull request" (give a title and a description, please).
 - That's all; I will have your contribution and I will try to merge it into the development branch of OpenSimMPLS. Please, comment your contribution as much as possible; I have to be able to understand your contribution.

REMEMBER!!!! all your contributions have to be compatible with Apache Software License 2.0 and you have to own all rights on them. And no source code contributions has to be compatible to Creative Commons - By.

# WHAT CAN YOU CONTRIBUTE?

Lot of things. Most people doesn't have the possibility to contribute code. But there are lots of other thigs that are very important too:

 - Source code.
 - Translations.
 - Scenarios already designed.
 - Teaching units. Lots of <b>OpenSimMPLS</b> users are university techers. They use <b>OpenSimMPLS to teach network subjects</b>. If this is your case, share with everybody your teaching units: things you ask your students, exams, exercises based on <b>OpenSimMPLS</b>, and so on.
 - Documentation. Independently of your native language, there is an <b>OpenSimMPLS</b> user that also have the same native language. I've not the possibility of documenting everything, but if you do that, share it!
 - Presentations.
 - New icons and graphics art.
 - Ideas.

#### Thanks for contributing.
