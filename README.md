## Factal Maker

An interactive GUI for creating neat fractal images.

#### Overview

Fractals, as they relate to this project, are cool mathematical figures that exhibit "self-similarity". This means that fractals are composed of smaller versions of themselves, or that there are many similar figures within a fractal. The purpose of this project was to create a versatile fractal "editor" that allows the user to create interesting fractals with the use of a few simple rules. The following explains the user interface and logic of the program.

### Fractal Generator Model

Any fractal created by my program is defined by a "generator". A generator is a set of vectors drawn in the x/y plane. Each of these vectors has certain attributes that define how they behave, and exacly one of these serves as a reference vector. Details will be explained later. For now, lets take a look at an example:

<div>
![](https://raw.githubusercontent.com/mitrydoug/fractal-maker/master/images/fractal1.png)
</div>
