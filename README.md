# Project status (compiled using Java 1.8)

- Master branch [![Build Status](https://travis-ci.org/manolodd/opensimmpls.svg?branch=master)](https://travis-ci.org/manolodd/opensimmpls)

- Development (v2.0) branch [![Build Status](https://travis-ci.org/manolodd/opensimmpls.svg?branch=development_v2.0)](https://travis-ci.org/manolodd/opensimmpls)

# THE PROJECT

<b>OpenSimMPLS</b> is an MPLS network simulator, multiplatform and mutilanguage. It is easy-to-use and is intended for teaching activities. It can be used as well to test new protocols, techniques and methods related to MPLS and/or GoS. OpenSimMPLS is a mature project used in universities and companies in more tan 130 different countries to teach, research and design MPLS networks. It was first developed in 2004 as part of a research project at the University of Extremadura. It was hosted since then in SourceForge.net as a CVS project and since 2014 it was updated as a GIT repository and hosted in GitHub forge.

![OpenSimMPLS logo](https://github.com/manolodd/opensimmpls/blob/master/src/imagenes/splash.png?raw=true)

# LICENSE
 
- <b>OpenSimMPLS 1.0</b> was released under the terms of GPLv2.0+.
- <b>OpenSimMPLS 1.1</b> was released under the terms of GPLv3.0+.
- <b>OpenSimMPLS 2.0</b> (Still unreleased. Only source code available) will be released under the terms of Apache Software License 2.0.


# People behind OpenSimMPLS

## Author:
    
 - Manuel Domínguez-Dorado - <ingeniero@ManoloDominguez.com>
   
## Other collaborators (only for release 1.0):

 - José Luis González Sánchez - <jlgs@unex.es>
 - Javier Carmona Murillo - <jcarmur@unex.es>
    
    
Please, refer always to the project home page at:

 - http://www.manolodominguez.com/projects/opensimmpls/

# Compiling from sources

You can download latest compiled stable releases from the releases section of 
this repository. However, if you want to test new features (please, do it and 
give feedback), you will need to compile the project from sources. Follow these 
steps:

 - Clone the OpenSimMPLS repo: 
```console
git clone https://github.com/manolodd/opensimmpls.git
```
 - Compile the code and obtain a binary jar including all you need (you will 
   need to install Ant before):
```console
cd opensimmpls
ant opensimmpls-binary-fat-release
```
 - The jar file will be located in binary-fat-release/opensimmpls directory.
```console
cd binary-fat-release/opensimmpls
```
- Now, run the simulator:
```console
java -jar openSimMPLS-bin-v{YourVersion}.jar
```
- Need some scenarios to try? There is not problem!! You'll find them at the
  examples folder (root of your cloned repository).


#HOW TO CONTRIBUTE


OpenSimMPLS is opensource software. We encourage you to modify it as much as 
possible; but We would like you to send this modifications back and, hence, 
became an OpenSimMPLS contributor. In this way, all the people will benefit from
them as you are doing downloading and using OpenSimMPLS now.

If you want to contribute to OpenSimMPLS project, follow these instructions:

 - Log in to your GitHub account.
 - Look for OpenSimMPLS project.
 - Create a fork of OpenSimMPL in your own GitHub repository.
 - Clone your OpenSimMPLS repository to your PC or laptop.
 - Create a branch in your local cloned GIT repository. We recommend to name 
   this branch as "opensimmpls-festureyouaredeveloping" or something similar.
 - Do all modifications on this branch, file additions or deletions, 
   modifications, commits...
 - Push your modifications to your remote github OpenSimMPLS repository.
 - Go again to yout GitHub account, choose your OpenSimMPLS repository and then
   your "opensimmpls-festureyouaredeveloping" branch (since the previous step, 
   this branch should be there) and click on the green button at the left. This 
   will guide you to make a pull request (send your modifications on your own
   OpenSimMPLS repository to OpenSimMPLS main repository from where you did your
   fork at the beginning).
 - That's all we will have your contribution and will try to merge it into the
   master branch of OpenSimMPLS. Please, comment your contribution as much as
   possible; we should understand your contribution.

This is a very easy process. However, if it is very difficult to you, simply 
send us all your modifications (scenario, sources, documentation...) to:

opensimmpls@manolodominguez.com

And we will do our best to understand them and, somewhat, include your 
contribution into the project.

REMEMBER!!!! all your contributions have to be compatible with Apache Software License 2.0.

#### Thanks for contributing.
