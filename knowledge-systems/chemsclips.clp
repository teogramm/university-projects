; BOUAS CHRISTOS 3315
; GRAMMENOS THEODOROS 3294
; Wed Dec 11 12:34:59 EET 2019
; 
;+ (version "3.5")
;+ (build "Build 663")


(defclass chemical
	(is-a USER)
	(role concrete)
	(multislot colour
		(type SYMBOL)
		(allowed-values clear red white)
		(default clear)
		(create-accessor read-write))
	(multislot specific_gravity
		(type SYMBOL)
		(allowed-values equal_to_1 above_1 below_1)
		(default equal_to_1)
		(create-accessor read-write))
	(multislot radioactivity
		(type SYMBOL)
		(allowed-values no yes)
		(default no)
		(create-accessor read-write))
	(multislot pH-high
		(type INTEGER)
		(range 0 14)
		(create-accessor read-write))
	(multislot pH-low
		(type INTEGER)
		(range 0 14)
		(create-accessor read-write))
	(multislot solubility
		(type SYMBOL)
		(allowed-values soluble insoluble)
		(default soluble)
		(create-accessor read-write))
	(multislot smell
		(type SYMBOL)
		(allowed-values none vinegar choking)
		(default none)
		(create-accessor read-write))
	(multislot hazards
		(type SYMBOL)
		(allowed-values asphyxiation burns_skin explosive highly_toxic_PCBs)
		(create-accessor read-write))
	(multislot spectrometry
		(type SYMBOL)
		(allowed-values none sulphur carbon sodium metal)
		(default none)
		(create-accessor read-write))
	(multislot suspect
		(type SYMBOL)
		(allowed-values no yes)
		(default no)
		(create-accessor read-write)))

(defclass acid
	(is-a chemical)
	(role concrete)
	(multislot pH-high
		(type INTEGER)
		(range 0 14)
		(default 6)
		(create-accessor read-write))
	(multislot pH-low
		(type INTEGER)
		(range 0 14)
		(default 0)
		(create-accessor read-write)))

(defclass strong_acid
	(is-a acid)
	(role concrete)
	(multislot pH-high
		(type INTEGER)
		(range 0 14)
		(default 3)
		(create-accessor read-write))
	(multislot hazards
		(type SYMBOL)
		(allowed-values asphyxiation burns_skin explosive highly_toxic_PCBs)
		(default burns_skin)
		(create-accessor read-write)))

(defclass weak_acid
	(is-a acid)
	(role concrete)
	(multislot pH-low
		(type INTEGER)
		(range 0 14)
		(default 3)
		(create-accessor read-write)))

(defclass base
	(is-a chemical)
	(role concrete)
	(multislot pH-high
		(type INTEGER)
		(range 0 14)
		(default 14)
		(create-accessor read-write))
	(multislot pH-low
		(type INTEGER)
		(range 0 14)
		(default 8)
		(create-accessor read-write)))

(defclass strong_base
	(is-a base)
	(role concrete)
	(multislot pH-low
		(type INTEGER)
		(range 0 14)
		(default 11)
		(create-accessor read-write))
	(multislot hazards
		(type SYMBOL)
		(allowed-values asphyxiation burns_skin explosive highly_toxic_PCBs)
		(default burns_skin)
		(create-accessor read-write)))

(defclass weak_base
	(is-a base)
	(role concrete)
	(multislot pH-high
		(type INTEGER)
		(range 0 14)
		(default 11)
		(create-accessor read-write)))

(defclass oil
	(is-a chemical)
	(role concrete)
	(pattern-match reactive)
	(multislot pH-high
		(type INTEGER)
		(range 0 14)
		(default 8)
		(create-accessor read-write))
	(multislot pH-low
		(type INTEGER)
		(range 0 14)
		(default 6)
		(create-accessor read-write))
	(multislot solubility
		(type SYMBOL)
		(allowed-values soluble insoluble)
		(default insoluble)
		(create-accessor read-write))
	(multislot spectrometry
		(type SYMBOL)
		(allowed-values none sulphur carbon sodium metal)
		(default carbon)
		(create-accessor read-write)))

(defclass node
	(is-a USER)
	(role concrete)
	(multislot downstream
		(type INSTANCE)
		(create-accessor read-write))
  (multislot suspect
	  (type SYMBOL)
	  (allowed-values no yes)
	  (default no)
	  (create-accessor read-write)))

(defclass store
	(is-a node)
	(role concrete)
	(multislot contents
		(type INSTANCE)
		(cardinality 1 ?VARIABLE)
		(create-accessor read-write)))

(defclass manhole
	(is-a node)
	(role concrete))

; Wed Dec 11 12:34:59 EET 2019
; 
;+ (version "3.5")
;+ (build "Build 663")

(definstances facts
([acetic_acid] of  weak_acid

	(smell vinegar))

([aluminium_hydroxide] of  weak_base

	(colour white)
	(specific_gravity above_1)
	(spectrometry metal))

([carbonic_acid] of  weak_acid

	(spectrometry carbon))

([chromogen_23] of  weak_base

	(colour red)
	(specific_gravity below_1))

([hydrochloric_acid] of  strong_acid

	(hazards asphyxiation burns_skin)
	(smell choking))

([manhole_1] of  manhole

	(downstream [manhole_9]))

([manhole_10] of  manhole

	(downstream [manhole_12]))

([manhole_11] of  manhole

	(downstream [manhole_13]))

([manhole_12] of  manhole

	(downstream [monitoring_station]))

([manhole_13] of  manhole

	(downstream [monitoring_station]))

([manhole_2] of  manhole

	(downstream [manhole_9]))

([manhole_3] of  manhole

	(downstream [manhole_9]))

([manhole_4] of  manhole

	(downstream [manhole_10]))

([manhole_5] of  manhole

	(downstream [manhole_10]))

([manhole_6] of  manhole

	(downstream [manhole_11]))

([manhole_7] of  manhole

	(downstream [manhole_11]))

([manhole_8] of  manhole

	(downstream [manhole_13]))

([manhole_9] of  manhole

	(downstream [manhole_12]))

([monitoring_station] of  node
)

([petrol] of  oil

	(hazards explosive)
	(specific_gravity below_1))

([rubidium_hydroxide] of  weak_base

	(radioactivity yes)
	(specific_gravity above_1)
	(spectrometry metal))

([sodium_hydroxide] of  strong_base

	(spectrometry sodium))

([store_1] of  store

	(contents
		[sulphuric_acid]
		[petrol])
	(downstream [manhole_1]))

([store_2] of  store

	(contents
		[hydrochloric_acid]
		[acetic_acid])
	(downstream [manhole_2]))

([store_3] of  store

	(contents
		[rubidium_hydroxide]
		[transformer_oil])
	(downstream [manhole_3]))

([store_4] of  store

	(contents
		[carbonic_acid]
		[acetic_acid]
		[petrol])
	(downstream [manhole_4]))

([store_5] of  store

	(contents
		[chromogen_23]
		[sulphuric_acid]
		[petrol])
	(downstream [manhole_5]))

([store_6] of  store

	(contents
		[aluminium_hydroxide]
		[transformer_oil]
		[carbonic_acid])
	(downstream [manhole_6]))

([store_7] of  store

	(contents
		[hydrochloric_acid]
		[sulphuric_acid])
	(downstream [manhole_7]))

([store_8] of  store

	(contents
		[acetic_acid]
		[carbonic_acid]
		[sodium_hydroxide])
	(downstream [manhole_8]))

([sulphuric_acid] of  strong_acid

	(spectrometry sulphur))

([transformer_oil] of  oil

	(hazards highly_toxic_PCBs))
)

;; DIKOS MAS KODIKAS

(deffunction ask-question (?question $?allowed-values)
   (printout t ?question)
   (bind ?answer (read))
   (if (lexemep ?answer)
       then (bind ?answer (lowcase ?answer)))
   (while (not (member ?answer ?allowed-values)) do
      (printout t ?question)
      (bind ?answer (read))
      (if (lexemep ?answer)
          then (bind ?answer (lowcase ?answer))))
   ?answer)

(deffunction ask-question-range (?question $?allowed-values)
   (printout t ?question)
   (bind ?answer (read))
   (if (lexemep ?answer)
       then (bind ?answer (lowcase ?answer)))
   (while (not (and (>= ?answer (nth$ 1 ?allowed-values)) (<= ?answer (nth$ 2 ?allowed-values)))) do
      (printout t ?question)
      (bind ?answer (read))
      (if (lexemep ?answer)
          then (bind ?answer (lowcase ?answer))))
   ?answer)

(deffunction ask-multiple-choice-question (?question $?allowed-values)
   (printout t ?question)
   (bind ?answer (readline))
   (bind $?answers (explode$ ?answer))
   (while (not (subsetp ?answers ?allowed-values)) do
      (printout t ?question)
      (bind ?answer (readline))
      (bind $?answers (explode$ ?answer)))
   (return $?answers)
)

(deffunction get_suspect_inputs (?node)
  ; Vres ola ta instances ta opoia exoun downstream to x, diladi ola ta inputs sto x, kai ta opoia einai suspect=yes
  ; Gia kapoio logo den doulebei to (eq ?x:suspect yes) opote to kanoume me to member$.
  (bind ?inputs (find-all-instances ((?x node)) (and (member$ ?node ?x:downstream) (member$ yes ?x:suspect))))
  (return ?inputs)
)

(deffunction get_enoho_freatio ($?suspects)
  (bind ?found 0)
  (while (= ?found 0) do
    (printout t "Yparhei molynsi sto freation " (first$ ?suspects) "? ")
    (bind ?answer (read))
    (if (eq ?answer yes) then
        (return (first$ ?suspects)))
  )
)

;; RULES START HERE

(defrule initial-rule
  ?x <- (initial-fact)
  =>
     (retract ?x)
     (set-strategy mea)
     (assert (goal ask-questions))
)

;; kanoume tin arxiki erotisi, opou zitame apo ton xristi gia poia xaraktiristika thelei na dosei times
;; episis kanoume arxikopoihsh ola ta gegonota pou antistoixoun se xaraktiristika se kapoia arxiki timi
;; i arxiki timi einai -1 gia to pH (pou dexetai times se kapoio euros)
;; i arxiki timi gia ta ypoloipa einai undef

(defrule initial-question
  ?x <- (goal ask-questions)
  =>
    (retract ?x)
    (bind $?answer (ask-multiple-choice-question "Gia poies metriseis tha dothoyn times (pH solubility spectrometry colour smell specific_gravity radioactivity)? " pH solubility spectrometry colour smell specific_gravity radioactivity) )
	(assert (metriseis $?answer))
	(assert (goal get-metriseis))
	(assert (pH -1))
	(assert (solubility undef))
	(assert (spectrometry undef))
	(assert (colour undef))
	(assert (smell undef))
	(assert (specific_gravity undef))
	(assert (radioactivity undef))
)

;; ZITAME TIMES GIA TIS METRISEIS POU THA DOSEI O XRISTIS

;; an entopizetai to onoma tou xaraktiristikou stin lista me tis metriseis pou tha dosei o xristis,
;; zitame timi gia to xaraktiristiko
;; episis afairoume apo tin facts-list to gegonos me tin arxiki timi gia to xaraktiristiko
;; kai prosthetoume neo gegonos me timi auti pou mas edose o xristis

(defrule ask-pH
  (goal get-metriseis)
  (metriseis $? pH $?)
  ?fact_to_remove <- (pH -1)
  =>
  (retract ?fact_to_remove)
  (bind $?allowed (slot-range chemical pH-high))
  (bind ?answer (ask-question-range (str-cat "Ti pH exei h molynsh  " "(" (implode$ ?allowed) "):" " ") ?allowed))
  (assert (pH ?answer))
  (printout t ?answer crlf)
)

(defrule ask-solubility
  (goal get-metriseis)
  (metriseis $? solubility $?)
  ?fact_to_remove <- (solubility undef)
  =>
  (retract ?fact_to_remove)
  (bind $?allowed (slot-allowed-values chemical solubility))
  (bind ?answer (ask-question (str-cat "Einai dialyth h molynsh  " "(" (implode$ ?allowed) "):" " ") ?allowed))
  (assert (solubility ?answer))
  (printout t ?answer crlf)
)

(defrule ask-spectrometry
  (goal get-metriseis)
  (metriseis $? spectrometry $?)
  ?fact_to_remove <- (spectrometry undef)
  =>
  (retract ?fact_to_remove)
  (bind $?allowed (slot-allowed-values chemical spectrometry))
  (bind ?answer (ask-question (str-cat "Dose apotelesma fasmatoskopias " "(" (implode$ ?allowed) "):" " ") ?allowed))
  (assert (spectrometry ?answer))
  (printout t ?answer crlf)
)

(defrule ask-colour
  (goal get-metriseis)
  (metriseis $? colour $?)
  ?fact_to_remove <- (colour undef)
  =>
  (retract ?fact_to_remove)
  (bind $?allowed (slot-allowed-values chemical colour))
  (bind ?answer (ask-question (str-cat "Ti xroma paratireitai " "(" (implode$ ?allowed) "):" " ") ?allowed))
  (assert (colour ?answer))
  (printout t ?answer crlf)
)

(defrule ask-smell
  (goal get-metriseis)
  (metriseis $? smell $?)
  ?fact_to_remove <- (smell undef)
  =>
  (retract ?fact_to_remove)
  (bind $?allowed (slot-allowed-values chemical smell))
  (bind ?answer (ask-question (str-cat "Ti osmh entopizetai " "(" (implode$ ?allowed) "):" " ") ?allowed))
  (assert (smell ?answer))
  (printout t ?answer crlf)
)

(defrule ask-specific_gravity
  (goal get-metriseis)
  (metriseis $? specific_gravity $?)
  ?fact_to_remove <- (specific_gravity undef)
  =>
  (retract ?fact_to_remove)
  (bind $?allowed (slot-allowed-values chemical specific_gravity))
  (bind ?answer (ask-question (str-cat "Ti eidiko varos exei h molynsh  " "(" (implode$ ?allowed) "):" " ") ?allowed))
  (assert (specific_gravity ?answer))
  (printout t ?answer crlf)
)

(defrule ask-radioactivity
  (goal get-metriseis)
  (metriseis $? radioactivity $?)
  ?fact_to_remove <- (radioactivity undef)
  =>
  (retract ?fact_to_remove)
  (bind $?allowed (slot-allowed-values chemical radioactivity))
  (bind ?answer (ask-question (str-cat "Einai radienergos h molynsh  " "(" (implode$ ?allowed) "):" " ") ?allowed))
  (assert (radioactivity ?answer))
  (printout t ?answer crlf)
)

(defrule get-metriseis-continue
  ?x <- (goal get-metriseis)
  =>
  (retract ?x)
  (assert (goal find-suspects))
)

;; ME VASI TA GEGONOTA POU EXOUN EISAXTHEI STO PROIGOUMENO VIMA, EPILEGONTAI TA PITHANA XIMIKA

;; elegxoume gia acids
;; theloume sygkekrimena oria gia to pH (an mas exei dosei timi o xristis)
;; elegxoume gia ola ta ypoloipa xaraktiristika an i timi pou mas edose o xristis tairiazei me tin timi kapoiou acid sta instances.
;; an tairiazei i den mas edose timi gia to xaraktiristiko o xristis, 
;; den boroume na apoklisoume to stoixeio exaitias autou tou xaraktiristikou.
;; opote kai pame sto epomeno xaraktiristiko

(defrule find-acids
  (goal find-suspects)
  (object (is-a acid) (name ?x) (solubility ?solub) (spectrometry ?spectr) (colour ?colour) (smell ?smell) (specific_gravity ?sg) (radioactivity ?radio))
  (pH ?given_pH)
  (solubility ?given_solub)
  (spectrometry ?given_spectr)
  (colour ?given_colour)
  (smell ?given_smell)
  (specific_gravity ?given_sg)
  (radioactivity ?given_radio)
  (or (test (and (>= ?given_pH 0) (< ?given_pH 6))) (test (= ?given_pH -1)))
  (or (test (eq ?given_solub ?solub)) (test (eq ?given_solub undef)))
  (or (test (eq ?given_spectr ?spectr)) (test (eq ?given_spectr undef)))
  (or (test (eq ?given_colour ?colour)) (test (eq ?given_colour undef)))
  (or (test (eq ?given_smell ?smell)) (test (eq ?given_smell undef)))
  (or (test (eq ?given_sg ?sg)) (test (eq ?given_sg undef)))
  (or (test (eq ?given_radio ?radio)) (test (eq ?given_radio undef)))
  =>
  (modify-instance ?x (suspect yes))
)

;; elegxoume an yparxoun suspect oils
;; omoia me ta acids. allazoun ta oria pH pou theloume.
;; episis einai hardcoded to oti den theloume to solubility na exei timi soluble (an mas exei dosei timi o xristis)
;; kai to spectrometry na deixnei carbon (an mas exei dosei timi o xristis)

(defrule find-oils
  (goal find-suspects)
  (object (is-a oil) (name ?x) (colour ?colour) (smell ?smell) (specific_gravity ?sg) (radioactivity ?radio))
  (pH ?given_pH)
  (colour ?given_colour)
  (smell ?given_smell)
  (specific_gravity ?given_sg)
  (radioactivity ?given_radio)
  (or (test (and (>= ?given_pH 6) (< ?given_pH 8))) (test (= ?given_pH -1)))
  (or (solubility insoluble) (solubility undef))
  (or (spectrometry carbon) (spectrometry undef))
  (or (test (eq ?given_colour ?colour)) (test (eq ?given_colour undef)))
  (or (test (eq ?given_smell ?smell)) (test (eq ?given_smell undef)))
  (or (test (eq ?given_sg ?sg)) (test (eq ?given_sg undef)))
  (or (test (eq ?given_radio ?radio)) (test (eq ?given_radio undef)))
  =>
  (modify-instance ?x (suspect yes))
)

;; elegxoume gia bases
;; omoi me ta acids, apla allazoun ta oria tou pH pou theloume

(defrule find-bases
  (goal find-suspects)
  (object (is-a base) (name ?x) (solubility ?solub) (spectrometry ?spectr) (colour ?colour) (smell ?smell) (specific_gravity ?sg) (radioactivity ?radio))
  (pH ?given_pH)
  (solubility ?given_solub)
  (spectrometry ?given_spectr)
  (colour ?given_colour)
  (smell ?given_smell)
  (specific_gravity ?given_sg)
  (radioactivity ?given_radio)
  (or (test (and (>= ?given_pH 8) (<= ?given_pH 14))) (test (= ?given_pH -1)))
  (or (test (eq ?given_solub ?solub)) (test (eq ?given_solub undef)))
  (or (test (eq ?given_spectr ?spectr)) (test (eq ?given_spectr undef)))
  (or (test (eq ?given_colour ?colour)) (test (eq ?given_colour undef)))
  (or (test (eq ?given_smell ?smell)) (test (eq ?given_smell undef)))
  (or (test (eq ?given_sg ?sg)) (test (eq ?given_sg undef)))
  (or (test (eq ?given_radio ?radio)) (test (eq ?given_radio undef)))
  =>
  (modify-instance ?x (suspect yes))
)

(defrule find-suspects-continue
  ?x <- (goal find-suspects)
  =>
  (retract ?x)
  (assert (goal make-suspects))
)

; Enoxopoihse prota tis apothikes pou echoun ta ypopta chimika

(defrule suspect-stores
  (goal make-suspects)
  (object (is-a chemical)
          (name ?chemname)
          (suspect yes))
  (object (is-a store)
          (name ?storename)
          (contents $? ?chemname $?))
  =>
  (modify-instance ?storename (suspect yes))
  (printout t "Store suspected: " ?storename crlf)
)

; Sti synexeia vres ola ta nodes pou einai suspect kai kataligei
; se afta kapoio node pou den einai suspect. Tote kane kai afto 
; suspect.

(defrule propagate-suspects
  (goal make-suspects)
  (object (is-a node)
      (name ?unsuspect)
      (suspect no))
  (object (is-a node)
      (name ?suspect)
      (suspect yes)
      (downstream $? ?unsuspect $?))
  =>
  (modify-instance ?unsuspect (suspect yes))
  (printout t "Suspected: " ?unsuspect crlf)
)

(defrule make-suspects-continue
  ?x <- (goal make-suspects)
  =>
  (retract ?x)
  (assert (goal backtrack))
  ; To current mas deixnei se poio i poia nodes vriskomaste
  (assert (current [monitoring_station]))
)

; Xekina na pigaineis pros ta piso

(defrule backtrack-multiple-suspects
  (goal backtrack)
  (current ?cur)
  (test (> (length$ (get_suspect_inputs ?cur)) 1))
  =>
  (bind ?inputs (get_suspect_inputs ?cur))
  (bind ?enoho (get_enoho_freatio ?inputs))
  (assert (current ?enoho))
  (printout t "Pigainw sto " ?enoho crlf)
)

(defrule backtrack-single-suspect
  (goal backtrack)
  (current ?cur)
  (test (= (length$ (get_suspect_inputs ?cur)) 1))
  =>
  (bind ?inputs (get_suspect_inputs ?cur))
  (assert (current (first$ ?inputs)))
    (printout t "Pigainw sto " (first$ ?inputs) crlf)
)

(defrule backtrack-continue
  ?x <- (goal backtrack)
  =>
  (retract ?x)
  (assert (goal print-results))
)

(defrule print-suspect-chems
  (goal print-results)
  (object (is-a chemical) 
          (name ?x)
          (hazards $?h)
          (suspect yes))
  =>
  (printout t "To xhmiko poy prokalese th molynsh pithanon na einai to " ?x crlf)
  (if (> (length$ ?h) 0)
      then
      (printout t "Pithanoi kindynoi: " (implode$ ?h) crlf)
  )
;;  (printout t "Pithanoi kindynoi: " ?hazards crlf) | with this-> (hazards ?hazards) inside (object) fact above
)
