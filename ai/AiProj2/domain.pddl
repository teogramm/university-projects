; Domain description
; Describe the relations and transitions that can occur
(define (domain elevators) ; Domain name must match problem's

  (:requirements
    :strips                 ; basic preconditions and effects
    ;:negative-preconditions ; to use not in preconditions
    ;:equality               ; to use = in preconditions
  )

  ; Define the relations
  ; Question mark prefix denotes free variables
  (:predicates
    (person ?p)
    (elevator ?e)
    (floor ?f)
    
    (at ?f ?e)      ; Elevator ?e is at floor ?f
    (in ?e ?p)      ; Person ?p is in elevetor ?e
    (on ?f ?p)      ; Person ?p is on floor ?f
    (serves ?e ?f);Elvator ?e serves floor ?f
  )

  (:action move
    :parameters(?elev ?from ?to)
    :precondition(and
        (elevator ?elev)
        (floor ?from)
        (floor ?to)
        (serves ?elev ?to)
        (at ?from ?elev)
    )
    :effect(and
        (at ?to ?elev)
        (not (at ?from ?elev))
    )
  )
  
  (:action enter
    :parameters(?elev ?person ?floor)
    :precondition(and
        (elevator ?elev)
        (person ?person)
        (floor ?floor)
        (at ?floor ?elev)
        (on ?floor ?person)
    )
    :effect(and
        (in ?elev ?person)
        (not (on ?floor ?person))
    )
  )
  
  (:action exit
    :parameters(?elev ?person ?floor)
    :precondition(and
        (elevator ?elev)
        (person ?person)
        (floor ?floor)
        (at ?floor ?elev)
        (in ?elev ?person)
    )
    :effect(and
        (on ?floor ?person)
        (not (in ?elev ?person))
    )
  )
)
