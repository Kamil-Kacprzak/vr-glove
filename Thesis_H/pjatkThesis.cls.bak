\NeedsTeXFormat{LaTeX2e}
\ProvidesClass{pjatkThesis}[2016/6/21 Thesis]

%\RequirePackage[resetfonts]{cmap} %użycie link w zakladkach
\RequirePackage[utf8]{inputenc}
%\RequirePackage{lmodern, cmap}
\RequirePackage[T1]{fontenc}


\RequirePackage{ifthen}
\RequirePackage{endnotes}

%\RequirePackage[pass,showframe]{geometry}

\RequirePackage{fancyhdr}
\newcommand{\ifthen}[2]{\ifthenelse{#1}{#2}{}}

\makeatletter

\RequirePackage{color} 

\newcounter{@@tcount}

%wymagane pole tryb plagiatu
%\newcommand{\@@plagiat}{unknown}
%\newcommand{\set@@plagiat}[1]{\def\@@plagiat{#1}}

% dostępne opcje wyboru
%\DeclareOption{pla}{\set@@plagiat{pla}}
%\DeclareOption{pub}{\set@@plagiat{pub}}

% wymagane pole katedra
\newcommand{\@@faculty}{unknown}
\newcommand{\set@@faculty}[1]{\def\@@faculty{#1}}

% dostępne opcje wyboru
\DeclareOption{inf}{\set@@faculty{inf}}
\DeclareOption{snm}{\set@@faculty{snm}}
\DeclareOption{aw}{\set@@faculty{aw}}
\DeclareOption{zi}{\set@@faculty{zi}}
\DeclareOption{kj}{\set@@faculty{kj}}

% wymagane pole specjalizacja
\newcommand{\@@spec}{unknown}
\newcommand{\set@@spec}[1]{\def\@@spec{#1}}

% dostępne opcje wyboru - inf
\DeclareOption{abd}{\set@@spec{abd}}
\DeclareOption{aio}{\set@@spec{aio}}
\DeclareOption{b}{\set@@spec{b}}
\DeclareOption{c}{\set@@spec{c}}
\DeclareOption{d}{\set@@spec{d}}
\DeclareOption{dw}{\set@@spec{dw}}
\DeclareOption{e}{\set@@spec{e}}
\DeclareOption{e3d}{\set@@spec{e3d}}
\DeclareOption{epg}{\set@@spec{epg}}
\DeclareOption{f}{\set@@spec{f}}
\DeclareOption{g}{\set@@spec{g}}
\DeclareOption{h}{\set@@spec{h}}
% dostępne opcje wyboru - zi
\DeclareOption{zp}{\set@@spec{zp}}
\DeclareOption{ec}{\set@@spec{ec}}
\DeclareOption{ab}{\set@@spec{ab}}
\DeclareOption{swdm}{\set@@spec{swdm}}
\DeclareOption{ws}{\set@@spec{ws}}

\ProcessOptions
% weryfikacja poprawności wyboru 

\setcounter{@@tcount}{0}
\ProcessOptions
\newcommand{\IW}[1]{\immediate\write16{#1}}{
  \catcode`\+=13
%  \def+{\space}

%  \ifthen{\(\equal{\@@plagiat}{unknown}\)}
%    {
%      \IW{}
%      \IW{Należy określić tryb pracy:}
%      \IW{+++ pla - dla pracy dla plagiatu}
%      \IW{+++ pub - dla pracy publikacyjnej}
%      \IW{++++\string\documentclass[wi]{pjatkThesis}}
%      \IW{The available school and department options are defined at}
%      \IW{++++http://engineering.purdue.edu/\string~mark/puthesis/\@@number Options}
%      \IW{Przerwano dalsze prace ...}
%      \IW{}
%      \stop
%    }

  \ifthen{\equal{\@@faculty}{unknown}}
    {
      \IW{}
      \IW{Należy określić wydział uczelni}
      \IW{++++\string\documentclass[wi]{pjatkThesis}}
%      \IW{The available school and department options are defined at}
%      \IW{++++http://engineering.purdue.edu/\string~mark/puthesis/\@@number Options}
      \IW{Przerwano dalsze prace ...}
      \IW{}
      \stop
    }
    
  \ifthen{\(\equal{\@@faculty}{inf} \and \equal{\@@spec}{unknown}\)}
    {
      \IW{}
      \IW{Należy określić specjalizację}
%      \IW{++++\string\documentclass[wi]{pjatkThesis}}
%      \IW{The available school and department options are defined at}
%      \IW{++++http://engineering.purdue.edu/\string~mark/puthesis/\@@number Options}
      \IW{Przerwano dalsze prace ...}
      \IW{}
      \stop
    }
    
  \ifthen{\(\equal{\@@faculty}{zi} \and \equal{\@@spec}{unknown}\)}
    {
      \IW{}
      \IW{Należy określić specjalizację dla wydziału zi}
%      \IW{++++\string\documentclass[wi]{pjatkThesis}}
%      \IW{The available school and department options are defined at}
%      \IW{++++http://engineering.purdue.edu/\string~mark/puthesis/\@@number Options}
      \IW{Przerwano dalsze prace ...}
      \IW{}
      \stop
    } 
}

%\newcommand{\institute}[1]{\def\@institute{#1}}
\newcommand{\spec}[1]{\def\@spec{#1}}
\newcommand{\album}[1]{\def\@album{#1}}
\newcommand{\type}[1]{\def\@type{#1}}
\newcommand{\supervisor}[1]{\def\@supervisor{#1}}
\newcommand{\location}[1]{\def\@location{#1}}

\LoadClass[12pt,a4paper,oneside]{report}

\newcommand{\maketitlepage}{

% definicja strony tytułowej
    %\newgeometry{hmarginratio=1:1}
    \begin{titlepage}

        %\setlength{\voffset}{-0.5in}
        %\setlength{\topmargin}{-0.2in}
        %\setlength{\textheight}{297mm}
        %\setlength{\textwidth}{170mm}
        %\setlength{\evensidemargin}{0.3in}
        %\setlength{\oddsidemargin}{0.3in}

        \begin{center}  
        \includegraphics[scale=.80]{head}
        \vfill
% Wydziały
        {\Large\bfseries\rmfamily\scshape 
            \ifthen{\equal{inf}{\@@faculty}}{Wydział Informatyki}
            \ifthen{\equal{snm}{\@@faculty}}{Wydział Sztuki Nowych Mediów}
            \ifthen{\equal{aw}{\@@faculty}}{Wydział Architektury Wnętrz}
            \ifthen{\equal{zi}{\@@faculty}}{Wydział Zarządzania Informacją}
            \ifthen{\equal{kj}{\@@faculty}}{Wydział Kultury Japonii}
        }\\[\baselineskip]
        
% Katedry
        {\Large\bfseries\rmfamily\scshape    
%            \ifthen{\(\equal{\@@faculty}{zi} \and \equal{\@@spec}{zp}\)}{Katedra ekonomii i zarządzania}
%            \ifthen{\(\equal{\@@faculty}{zi} \and \equal{\@@spec}{ec}\)}{Katedra ekonomii i zarządzania}
%            \ifthen{\(\equal{\@@faculty}{zi} \and \equal{\@@spec}{ab}\)}{Katedra Metod Programowania}
%            \ifthen{\(\equal{\@@faculty}{zi} \and \equal{\@@spec}{swdp}\)}{Katedra Metod Programowania}
%            \ifthen{\(\equal{\@@faculty}{zi} \and \equal{\@@spec}{ws}\)}{Katedra Organizacji Procesów Informacyjnych}        
%            \ifthenelse{\(\equal{\@@faculty}{inf} \and \equal{\@@spec}{h}\)}{Katedra Sieci Komputerowych}{Katedra - nieznana}
            \ifthenelse{\equal{\@@faculty}{inf}}{
                \ifthen{\equal{\@@spec}{abd}}{Katedra Baz Danych}
                \ifthen{\( \equal{\@@spec}{epg} \or 
                           \equal{\@@spec}{e2d} \or 
                           \equal{\@@spec}{e} \)}{Katedra Multimediów}
                \ifthen{\equal{\@@spec}{g}}{Katedra Metod Programowania}
                \ifthen{\equal{\@@spec}{h}}{Katedra Sieci Komputerowych}
            }{
                \ifthenelse{\equal{\@@faculty}{zi}}{
                    \ifthen{\(\equal{\@@spec}{zp} \or \equal{\@@spec}{ec} \)}{Katedra ekonomii i zarządzania}
                    \ifthen{\(\equal{\@@spec}{ab} \or \equal{\@@spec}{swdp} \)}{Katedra Metod Programowania}
                    \ifthen{\equal{\@@spec}{ws}}{Katedra Organizacji Procesów}
                }{
      \IW{}
      \IW{Należy określić specjalizację dla wydziału zi1}
%      \IW{++++\string\documentclass[wi]{pjatkThesis}}
%      \IW{The available school and department options are defined at}
%      \IW{++++http://engineering.purdue.edu/\string~mark/puthesis/\@@number Options}
      \IW{Przerwano dalsze prace ...}
      \IW{}
      \stop
                }
            }
        }\\[\baselineskip]  
        
% Specjalizacje
        {\large\bfseries\rmfamily\scshape
            \ifthenelse{\equal{\@@faculty}{inf}}{
                \ifthenelse{\equal{\@@spec}{abd}}{Bazy danych}{
                \ifthenelse{\equal{\@@spec}{aio}}{Inżynieria oprogramowania i baz danych}{
                \ifthenelse{\equal{\@@spec}{b}}{Programowanie systemowe i sieciowe}{
                \ifthenelse{\equal{\@@spec}{c}}{Systemy rozproszone i równoległe}{
                \ifthenelse{\equal{\@@spec}{d}}{Inteligentne systemy przetwarzania danych}{
                \ifthenelse{\equal{\@@spec}{dw}}{Eksploracja WWW}{
                \ifthenelse{\equal{\@@spec}{e}}{Multimedia}{
                \ifthenelse{\equal{\@@spec}{e2d}}{Multimedia – Animacja 3D}{
                \ifthenelse{\equal{\@@spec}{epg}}{Multimedia – Programowanie gier}{
                \ifthenelse{\equal{\@@spec}{f}}{Inteligentne metody inżynierii wiedzy}{
                \ifthenelse{\equal{\@@spec}{g}}{Programowanie aplikacji biznesowych}{
                \ifthenelse{\equal{\@@spec}{h}}{Sieci urządzeń mobilnych}{
      \IW{}
      \IW{Użyty skrót opisujący specjalizację nie występuje w wykazie specjalizacji wydziału informatyki.}
      \IW{Przerwano dalsze prace ...}
      \IW{}
      \stop                
                }
                }}}}}}}}}}}
            }{
                \ifthenelse{\equal{\@@faculty}{zi}}{
                    \ifthenelse{\equal{\@@spec}{zp}}{Zarządzanie projektami}{
                    \ifthenelse{\equal{\@@spec}{swdm}}{Systemy wspomagania decyzji menedżerskich}{
                    \ifthenelse{\equal{\@@spec}{ec}}{E-commerce}{
                    \ifthenelse{\equal{\@@spec}{ab}}{Analiza biznesowa}{
                    \ifthenelse{\equal{\@@spec}{ws}}{Wdrożenia systemów IT}{
      \IW{}
      \IW{Użyty skrót opisujący specjalizację nie występuje w wykazie specjalizacji wydziału Zarządzania Informacją.}
      \IW{Przerwano dalsze prace ...}
      \IW{}
      \stop               
                    }
                    }}}}
                }{
      \IW{}
      \IW{Należy określić specjalizację dla wydziału zi3}
%      \IW{++++\string\documentclass[wi]{pjatkThesis}}
%      \IW{The available school and department options are defined at}
%      \IW{++++http://engineering.purdue.edu/\string~mark/puthesis/\@@number Options}
      \IW{Przerwano dalsze prace ...}
      \IW{}
      \stop
                }
            }
%            \ifthenelse{\( \equal{\@@faculty}{inf} \and \equal{\@@spec}{unknown}\)}{nieznana}{???}
        }\\[\baselineskip]
        \vfill
        {\Large\bfseries\rmfamily \@author}\\
        {\large \@album}\\[\baselineskip]
        \vfill
        {\LARGE\bfseries\rmfamily \@title}\\[\baselineskip]
        %\vspace*{2cm}
        \vfill
        \end{center}
        \begin{minipage}{0.4\textwidth}
        \begin{flushleft}
        \end{flushleft}
        \end{minipage}
        \begin{minipage}{0.4\textwidth}
        \begin{flushright}
        {\bfseries\small \@type}\\
        {\bfseries  \@supervisor}
        \end{flushright}
        \end{minipage}  
        
        \vfill
        %\vspace*{\fill}
        \begin{center}
        {\large\bfseries\rmfamily \@location, \@date}
        \end{center}
        
        \newpage\thispagestyle{empty}~
        \cleardoublepage
    \end{titlepage}
}

% koniec strony tytułowej

\renewcommand\tableofcontents{
	\thispagestyle{empty}
    \if@twocolumn
      \@restonecoltrue\onecolumn
    \else
      \@restonecolfalse
    \fi
  %  \pagestyle{fancyplain}
    \pagenumbering{roman}    
    \chapter*{\contentsname
        \@mkboth{\contentsname}{\contentsname}}%
    \@starttoc{toc}%
    \if@restonecol\twocolumn\fi
    
    \newpage\thispagestyle{empty}~
    \cleardoublepage    
}

\renewenvironment{abstract}{
  \thispagestyle{empty}
  \small
  \begin{center}
    \null\vspace{2em}
    \bfseries \abstractname\vspace{2em}\vspace{0pt}
  \end{center}
  \list{}{
    \setlength{\leftmargin}{15mm}
    \setlength{\rightmargin}{\leftmargin}
  }
  \item\relax
 }
 {\endlist}

\makeatother

\def\@chapter[#1]#2{%
  \ifnum \c@secnumdepth >\m@ne
    \refstepcounter{chapter}%
    \typeout{\@chapapp\space\thechapter.}%
    \setboolean{@@unset}{true}
    \ifthen{\equal{agecon}{\@@dept}}
      {\setboolean{@@unset}{false}\addcontentsline{toc}{chapter}{\protect\uppercase{\@chapapp\ \thechapter. #1}}}
    \ifthen{\equal{ce}{\@@dept}}
      {\setboolean{@@unset}{false}\addcontentsline{toc}{chapter}{\protect\numberline{\thechapter}\uppercase{#1}}}
    \ifthen{\equal{me}{\@@dept}}
      {\setboolean{@@unset}{false}\addcontentsline{toc}{chapter}{\protect\numberline{\thechapter.}\uppercase{#1}}}
    \ifthen{\equal{mse}{\@@dept}}
      {\setboolean{@@unset}{false}\addcontentsline{toc}{chapter}{\protect\uppercase{\@chapapp\ \thechapter. #1}}}
    \ifthen{\boolean{@@unset}}
      {\addcontentsline{toc}{chapter}{\protect\numberline{\thechapter}#1}}
  \else
    \ifthenelse{\equal{ce}{\@@dept}}
      {\addcontentsline{toc}{chapter}{\uppercase{#1}}}
      {\addcontentsline{toc}{chapter}{#1}}
  \fi
  \chaptermark{#1}%
  \addtocontents{lof}{\protect\addvspace{10\p@}}%
  \addtocontents{lot}{\protect\addvspace{10\p@}}%
  \ifthenelse{\equal{ce}{\@@dept}}
    {\@makechapterhead{\uppercase{#2}}}
    {\@makechapterhead{#2}}
  \@afterheading
  \ifthen{\not \boolean{@@inchapters}}
    {
      \pagenumbering{arabic}%
      \@@inchapterstrue
    }    
}

\renewcommand{\sectionmark}[1]{\markright{\it \thesection.\ #1}}
\renewcommand{\chaptermark}[1]{\markboth{\it \thechapter.\ #1}{}}
\renewcommand*\thesection{\thechapter.\arabic{section}}
\newcommand{\sectionbreak}{\cleardoublepage\phantomsection}

\AtBeginDocument{
    \maketitlepage
    \selectlanguage{polish} 
	\tableofcontents
	\include{Chapters/Abstrakt}

\selectlanguage{polish} 
\pagenumbering{arabic}
\baselineskip=22pt
    
\include{Chapters/Wstep}
    \pagestyle{fancyplain}
	\fancyhead[R]{}

\lfoot[\fancyplain{}{}]                 {\fancyplain{}{}}
\cfoot[\fancyplain{\thepage}{\thepage}] {\fancyplain{\thepage}{\thepage}}
\rfoot[\fancyplain{}{}]                 {\fancyplain{}{}}
}

\AtEndDocument{
\fancyhead{}
\renewcommand{\headrulewidth}{0pt}
\include{Chapters/Podsumowanie}
\addcontentsline{toc}{chapter}{Bibliografia}
\bibliographystyle{unsrt}
\bibliography{bibliografia}
\listoffigures
    %\cleardoublepage
    %\small

    %\bibliographystyle{abbrv}
    
    %\cleardoublepage
    %\addcontentsline{toc}{chapter}{Index}
    %\printindex
}