#
# Branched from Makefile template: @(#)Makefile.include	44.1	9/17/92
# SccsId:	@(#)Makefile	2.2	06 Jun 1996
#

SRCS		= 
OBJS		=
HDRS		= \
		  ec_ak135.P \
		  ec_ak135.PKKPab \
		  ec_ak135.PKKPbc \
		  ec_ak135.PKKPdf \
		  ec_ak135.PKKSab \
		  ec_ak135.PKKSbc \
		  ec_ak135.PKKSdf \
		  ec_ak135.PKPab \
		  ec_ak135.PKPbc \
		  ec_ak135.PKPdf \
		  ec_ak135.PKSab \
		  ec_ak135.PKSbc \
		  ec_ak135.PKSdf \
		  ec_ak135.PKiKP \
		  ec_ak135.PP \
		  ec_ak135.PP_B \
		  ec_ak135.PS \
		  ec_ak135.PcP \
		  ec_ak135.PcS \
		  ec_ak135.S \
		  ec_ak135.SKKPab \
		  ec_ak135.SKKPbc \
		  ec_ak135.SKKPdf \
		  ec_ak135.SKKSac \
		  ec_ak135.SKKSdf \
		  ec_ak135.SKPab \
		  ec_ak135.SKPbc \
		  ec_ak135.SKPdf \
		  ec_ak135.SKSac \
		  ec_ak135.SKSdf \
		  ec_ak135.SKiKP \
		  ec_ak135.SP \
		  ec_ak135.SS \
		  ec_ak135.SS_B \
		  ec_ak135.ScP \
		  ec_ak135.ScS \
		  ec_ak135.pP \
		  ec_ak135.pPKPab \
		  ec_ak135.pPKPbc \
		  ec_ak135.pPKPdf \
		  ec_ak135.pPKiKP \
		  ec_ak135.pS \
		  ec_ak135.pSKSac \
		  ec_ak135.pSKSdf \
		  ec_ak135.sP \
		  ec_ak135.sPKPab \
		  ec_ak135.sPKPbc \
		  ec_ak135.sPKPdf \
		  ec_ak135.sPKiKP \
		  ec_ak135.sS \
		  ec_ak135.sSKSac \
		  ec_ak135.sSKSdf

LIBS		=
MAN1		=
MAN3		=
STDLIBS		=
PROGRAM		=
LIBRARY		=
SUBDIRS		=

O		= -O
INCLUDES	=
CFLAGS		= $O $(INCLUDES)
CCFLAGS		= $O $(INCLUDES)
FFLAGS		= $O
LDFLAGS		= $O
LFLAGS		= -bchu $(INCLUDES)

COMPILE.c	= $(CC) $(CFLAGS) $(CPPFLAGS) -c
CC		= cc
CCC		= CC
F77		= f77
LD		= $(CC)
LINT		= lint
YACC		= yacc
LEX		= lex

MAN1DIR		= ../../doc/man/man1
MAN3DIR		= ../../doc/man/man3

LMODE		= 664
PMODE		= 775
GROUP		= aesir

GET		= sccs get
GFLAGS		=
SCLEAN		= sccs clean

RM		= rm
INSTALL		= install
MAKE		= make
PRINT		= lpr -p
AR		= ar
RANLIB		= ranlib
FLAGS		= $(MFLAGS) "CC=$(CC)" "F77=$(F77)" "O=$O" \
		  "GET=$(GET)" "GFLAGS=$(GFLAGS)" "SCLEAN=$(SCLEAN)" \
		  "LMODE=$(LMODE)" "PMODE=$(PMODE)" "GROUP=$(GROUP)" \
		  "PRINT=$(PRINT)" "MAKE=$(MAKE)"

.SUFFIXES:
.SUFFIXES:	.o .c .c~ .sc .sc~ .qc .qc~ .f .f~ .sf .sf~ .qf .qf~ .h .h~
.SUFFIXES:	.x .x~ .3 .3~ .1 .1~


.3~.3:
		$(GET) $(GFLAGS) $*.3

.1~.1:
		$(GET) $(GFLAGS) $*.1

all:		$(HDRS)

link:
		ln -s ec_ak135.P ec_ak135.Pn
		ln -s ec_ak135.S ec_ak135.Sn
		ln -s ec_ak135.PKPdf ec_ak135.PKP
		ln -s ec_ak135.PKKPdf ec_ak135.PKKP
		ln -s ec_ak135.PKKSdf ec_ak135.PKKS
		ln -s ec_ak135.PKSdf ec_ak135.PKS
		ln -s ec_ak135.SKPdf ec_ak135.SKP
		ln -s ec_ak135.SKSdf ec_ak135.SKS
		ln -s ec_ak135.SKKPdf ec_ak135.SKKP
		ln -s ec_ak135.SKKSdf ec_ak135.SKKS
		ln -s ec_ak135.pPKPdf ec_ak135.pPKP
		ln -s ec_ak135.pSKSdf ec_ak135.pSKS
		ln -s ec_ak135.sPKPdf ec_ak135.sPKP
		ln -s ec_ak135.sSKSdf ec_ak135.sSKS

subdir:
		-@for dir in $(SUBDIRS); do \
			(cd $$dir; echo "*** `pwd` -- `date`"; \
			$(MAKE) $(FLAGS) ); \
		done

install:	all link

clean:
		-$(RM) -f $(HDRS) ec_ak135.*

xclean:		clean
		$(SCLEAN)
		
lint:


print:
		$(HDRS) Makefile
		$(PRINT) $(HDRS) Makefile
sccs:
		$(GET) $(GFLAGS) $(HDRS) Makefile

