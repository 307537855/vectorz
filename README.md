# vectorz

Fast double-precision vector and matrix maths library for Java.

The intention of this library is for use in games, simulations, raytracers etc. 
where fast vector maths is important. 

Vectorz can do over *300 million* 3D vector operations per second.

### Example usage

    Vector3 v=Vector3.of(1.0,2.0,3.0);		
    v.normalise();                       // normalise v to a unit vector		
    Vector3 d=Vector3.of(10.0,0.0,0.0);		
    d.addMultiple(v, 5.0);               // d = d + (v * 5)
	System.out.println(d);	    

### Key features

 - Supports double vectors of arbitrary size
 - Vector values are mutable
 - Support for general matrices
 - Support for affine transformations
 - Ability to create lightweight "reference" vectors (e.g. to access subranges of other vectors)
 - Library of useful mathematical functions on vectors
 - Vectors have lots of utility functionality implemented - Cloneable, Serializable, Comparable etc.
 - Input / output of vectors and matrices in edn format

### Focus on performance

Vectorz is deigned to allow the maximum performance possible for vector / matrix maths on the JVM.

This focus has driven a number of important design decisions:

 - Specialised primitive-backed small vectors (1,2,3 and 4 dimensions) and matrixes (2x2, 3x3 and M*3)
 - Abstract base classes preferred over interfaces to allow more efficient dispatch
 - Multiple types of vector are provided for optimised performance in special cases
 - Hard-coded fast paths for most 2D and 3D operations
 - Vector operations are generally not thread safe, by design
 - Concrete classes are generally final
 
If you have a common case that isn't yet well optimised then please post an issue - the aim is to make all common operations efficient.

### Linear algebra

While Vectorz has some support for linear algebra and big matrix computations, this isn't the primary focus. 

If you are interested in doing mathematical computations with large matrices, you might want to check out:

 - http://code.google.com/p/efficient-java-matrix-library/
 - http://code.google.com/p/java-matrix-benchmark/