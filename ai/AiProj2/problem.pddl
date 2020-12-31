; Problem description
; Describe one scenario within the domain constraints
(define (problem elevator1 )
  (:domain elevators)

  ; p1 wants to go from 1 to floor 4
  ; p2 wants to go from 4 to ground
  ; p3 wants to go from floor 2 to floor 3
  ; p4 wants to go from floor 4 to floor 3
  ; p5 wants to go from floor 1 to floor 2
  (:objects p1 p2 p3 p4 p5 e1 e2 e3 fg f1 f2 f3 f4)

  ; The initial state describe what is currently true
  ; Everything else is considered false
  (:init
    ;Init objects
    (elevator e1)
    (elevator e2)
    (elevator e3)
    
    (floor fg)
    (floor f1)
    (floor f2)
    (floor f3)
    (floor f4)
    
    (person p1)
    (person p2)
    (person p3)
    (person p4)
    (person p5)
  
    ; Elevator 1 serves floors ground,1,3
    (serves e1 fg)
    (serves e1 f1)
    (serves e1 f3)
    
    ;Elevator 2 serves floors 2 and 4
    (serves e2 f4)
    (serves e2 f2)
    
    ;Elevator 3 serves floors 2 and ground
    (serves e3 fg)
    (serves e3 f2)

    ; Set up the people as described above
    (on f1 p1)
    (on f4 p2)
    (on f2 p3)
    (on f4 p4)
    (on f1 p5)
    
    ;All elevators start at ground floor
    (at fg e1)
    (at fg e2)
    (at fg e3)
  )

  ; The goal state describes what we desire to achieve
  (:goal (and
    ; Set up the destinations as described above
    (on f4 p1)
    (on fg p2)
    (on f3 p3)
    (on f3 p4)
    (on f2 p5)
  ))
)
