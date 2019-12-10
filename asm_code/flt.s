      .data
A:    .word 10
B:    .word 8
C:    .word 2
D:    .word 3
E:    .word 4
G:    .word 10000

      .text
main: l.d f2,A(r0)
      l.d f3,B(r0)
      l.d f4,C(r0)
      l.d f5,D(r0)
      l.d f6,E(r0)
	
      add.d f1,f2,f3
      mul.d f2,f4,f5
      add.d f3,f3,f4
      mul.d f6,f6,f6
      add.d f1,f3,f5
      add.d f2,f3,f4
      s.d f3,G(r0)
      halt          
