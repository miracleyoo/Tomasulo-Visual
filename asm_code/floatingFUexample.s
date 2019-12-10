;Floating FU test to debug and test highlights
;
	
	.data
number: .word 10
title:  .asciiz "floating FU test "

CONTROL: .word32 0x10000
DATA:    .word32 0x10008

        .text

	lwu r21,CONTROL(r0)
        lwu f21,CONTROL(r0)
        lwu f22,DATA(r0)
	daddi r5,r21,7
        add f24,r0,r21     
        add f20,f24,f24   
        sd f24,(r22)
        sd f20,(r21)

        mul f2,f24,2
	div f3,f24,2
        sd f2,(r3)
	sd f3,(r4)
	HALT

	