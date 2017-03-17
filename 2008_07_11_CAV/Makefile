# Makefile for papers
LATEX = pdflatex

# Run once, then re-run until it's happy
# Input redirected from /dev/null is like hitting ^C at first error
all:
	@echo $(LATEX) main.tex </dev/null
	@if $(LATEX) main.tex </dev/null; then \
		true; \
	else \
		stat=$$?; touch main.dvi; exit $$stat; \
	fi 
	#bibtex main
	@echo $(LATEX) main.tex </dev/null
	@while grep "Rerun to get cross" main.log; do \
		echo $(LATEX) main.tex </dev/null; \
		if $(LATEX) main.tex </dev/null; then \
			true; \
		else \
			stat=$$?; touch main.dvi; exit $$stat; \
		fi; \
	done
	@if grep "miktex" main.log ; then \
		cygstart main.pdf; \
	fi

clean:
	rm -f main.aux main.log main.out main.ps main.pdf main.dvi main.blg *.bak *.auto
