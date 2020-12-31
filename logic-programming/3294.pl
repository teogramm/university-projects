%%% ΓΡΑΜΜΕΝΟΣ ΘΕΟΔΩΡΟΣ ΑΕΜ 3294
% Σημείωση: όπου γράφω όνομα του σπιτιού εννοώ τη διεύθυνση
% του (πρώτο όρισμα στο γεγονός house).
:- dynamic request/10.
:- dynamic house/9.
:- dynamic bid/3.

:- [houses,requests].

% print_menu/0
% Εκτυπώνει το μενού της εφαρμογής.
print_menu :-
    nl,
    writeln("Μενού:"),
    writeln("======"),
    nl,
    writeln("1 - Προτιμήσεις ενός πελάτη"),
    writeln("2 - Μαζικές προτιμήσεις πελατών"),
    writeln("3 - Επιλογή πελατών μέσω δημοπρασίας"),
    writeln("0 - Έξοδος"),
    nl.

% read_number/2
% Διαβάζει έναν θετικό αριθμό ή 0 από το χρήστη, χωρίς περιορισμό
read_number(Ans,Prompt) :- 
    repeat,
    write(Prompt),
    read(Ans),
    (number(Ans), Ans >= 0),
    !.

% read_with_check/3
% Γράψε ένα μήνυμα και στη συνέχεια
% διάβασε μία μεταβλητή έως ότου ο χρήστης να δώσει μία 
% αποδεκτή απάντηση. Προσαρμογή από το παράδειγμα στις 
% διαφάνειες.
read_with_check(Ans,Prompt, Acceptable) :- 
    repeat,
    write(Prompt),
    read(Ans),
    (member(Ans, Acceptable);(write("Επιλέξτε ένα από τα "),write(Acceptable),nl,fail)),
    !.

% Replacing an element in a list (with 0-based indexing)
% https://www.swi-prolog.org/pldoc/doc_for?object=nth0/4
replace_nth0(List, Index, OldElem, NewElem, NewList) :-
    % predicate works forward: Index,List -> OldElem, Transfer
    nth0(Index,List,OldElem,Transfer),
    % predicate works backwards: Index,NewElem,Transfer -> NewList
    nth0(Index,NewList,NewElem,Transfer).

% create_list_of_element/2
% Δέχεται μία λίστα και ένα στοιχείο E.
% Παράγει μία λίστα ίδιου μεγέθους με αυτή που δόθηκε
% που σε κάθε θέση της περιέχει E.
create_list_of_element([_|[]],E, [E|[]]).
create_list_of_element([_| HNamesT], E, [E| HBiddersT]) :-
    create_list_of_element(HNamesT,E, HBiddersT).

% Επιτυγχάνει όταν δίνεται μία λίστα με λίστες
% και όλες οι λίστες είναι άδειες.
all_empty([[]]).
all_empty([[]|Tail]) :-
    all_empty(Tail).

% offer/1
% Δέχεται ως είσοδο το όνομα του αγοραστή.
% Διαβάζει τις προτιμήσεις ενός αγοραστή και τις εισάγει ως γεγονός.
offer(BName) :-
    read_number(MinArea, "Ελάχιστο εμβαδόν: "),
    read_number(MinBedroom, "Ελάχιστος αριθμός υπνοδωματίων: "),
    read_with_check(RPets, "Να επιτρέπονται κατοικίδια [yes/no]: ", [yes,no]),
    read_number(MinFloorElevator, "Από ποιον όροφο και πάνω να υπάρχει ανελκυστήρας; "),
    read_number(MaxRent, "Ποιο είναι το μέγιστο ενοίκιο που μπορείς να πληρώσεις; "),
    read_number(MaxRentCenter, "Πόσα θα έδινες για ένα διαμέρισμα στο κέντρο της πόλης (στα ελάχιστα τετραγωνικά); "),
    read_number(MaxRentSuburb, "Πόσα θα έδινες για ένα διαμέρισμα στα προάστια της πόλης (στα ελάχιστα τετραγωνικά); "),
    read_number(ExtraArea, "Πόσα θα έδινες για κάθε τετραγωνικό διαμερίσματος πάνω από το ελάχιστο; "),
    read_number(ExtraGarden, "Πόσα θα έδινες για κάθε τετραγωνικό κήπου; "),
    assertz(request(BName, MinArea, MinBedroom, RPets, MinFloorElevator, MaxRent, MaxRentCenter, MaxRentSuburb, ExtraArea, ExtraGarden)).

% delete_offer/1
% Διαγράφει ένα γεγονός request με αυτό το όνομα από τη μνήμη
delete_offer(RName) :-
    request(RName, MinArea, MinBedroom, RPets, MinFloorElevator, MaxRent, MaxRentCenter, MaxRentSuburb, ExtraArea, ExtraGarden),
    retract(request(RName, MinArea, MinBedroom, RPets, MinFloorElevator, MaxRent, MaxRentCenter, MaxRentSuburb, ExtraArea, ExtraGarden)).

% get_all_houses/1
% Τοποθετεί στη δοσμένη λίστα τα ονόματα όλων των σπιτιών που υπάρχουν στη βάση.
get_all_houses(HouseNames) :-
    findall(HName, house(HName,_, _, _, _, _, _, _, _), HouseNames).

% get_all_requests_names/1
% Τοποθετεί στη δοσμένη λίστα τα ονόματα όλων των γεγονότων request που 
% υπάρχουν στη βάση.
get_all_requests_names(RequestNames) :-
    findall(RName, request(RName,_,_,_,_,_,_,_,_,_), RequestNames).

% get_house_facts_from_names/2
% Επιστρέφει τα γεγονότα house που αντιστοιχούνε στα
% στοιχεία της λίστας
% HH = House Head
% FH = Fact Head
get_house_facts_from_names([HH|HT], [FH|FT]) :-
    house(HH,NBedrooms, Area, InCenter, Floor, Elevator, Pets, AreaGarden, Rent),
    FH = house(HH,NBedrooms, Area, InCenter, Floor, Elevator, Pets, AreaGarden, Rent),
    get_house_facts_from_names(HT,FT).
get_house_facts_from_names([],[]).

% get_house_names_from_facts/2
% Πετατρέπει μία λίστα με house facts σε μία λίστα με τα ονόματα 
% των σπιτιών.
% HH = House Head
% FH = Fact Head
get_house_names_from_facts([FH|FT], [HH|HT]) :-
    % Αποθήκευσε το όνομα του τρέχοντος σπιτιού στο HH
    arg(1,FH,HH),
    get_house_names_from_facts(FT,HT).
get_house_names_from_facts([],[]).


% print_houses/1
% Δέχεται μια λίστα με ονόματα κατάλληλων σπιτιών και εκτυπώνει λεπτομέρειες για αυτά.
print_houses([]).
print_houses([HHouse|THouse]) :-
    print_houses(THouse),
    nl,
    house(HHouse,NBedrooms, Area, InCenter, Floor, Elevator, HPets, AreaGarden, Rent),
    write("Κατάλληλο σπίτι στην διεύθυνση: "), write(HHouse), nl,
    write("Υπνοδωμάτια: "), write(NBedrooms), nl,
    write("Εμβαδόν: "), write(Area), nl,
    write("Εμβαδόν κήπου: "), write(AreaGarden), nl,
    write("Είναι στο κέντρο της πόλης: "), write(InCenter), nl,
    write("Επιτρέπονται κατοικίδια: "), write(HPets), nl,
    write("Όροφος: "), write(Floor), nl,
    write("Ανελκυστήρας: "), write(Elevator), nl,
    write("Ενοίκιο: "), write(Rent), nl.

% print_houses_with_empty_message
% Εκτυπώνει τα σπίτια της λίστας ή 
% βγάζει κατάλληλο μήνυμα αν η λίστα
% εισόδου είναι άδεια.
print_houses_with_empty_message([]) :-
    writeln("Δεν υπάρχει κατάλληλο σπίτι!")
    ,nl.

% print_houses_with_empty_message
% Εκτυπώνει τα σπίτια της λίστας ή 
% βγάζει κατάλληλο μήνυμα αν η λίστα
% εισόδου είναι άδεια.
print_houses_with_empty_message([H|T]) :-
    print_houses([H|T]),
    nl.

% print_recommended_houses/1
% Βγάζει μήνυμα που προτείνει την ενοικίαση
% των δοσμένων σπιτιών.
% Δέχεται ως είσοδο λίστα με τα ονόματα (διεύθυνση) των σπιτιών.
print_recommended_houses(Houses) :-
    write("Προτείνεται η ενοικίαση του διαμερίσματος στην διεύθυνση: "),
    write(Houses),
    nl.

% compatible_house/2
% Δέχεται ως είσοδο τα ονόματα και τα αντοιστοιχίζει στα
% κατάλληλα γεγονότα house και request
compatible_house(HName, RName) :-
    house(HName,NBedrooms, Area, InCenter, Floor, Elevator, HPets, AreaGarden, Rent),
    request(RName, MinArea, MinBedroom, RPets, MinFloorElevator, MaxRent, MaxRentCenter, MaxRentSuburb, ExtraArea, ExtraGarden),
    compatible_house(house(HName,NBedrooms, Area, InCenter, Floor, Elevator, HPets, AreaGarden,Rent),
                     request(RName, MinArea, MinBedroom, RPets, MinFloorElevator, MaxRent, MaxRentCenter, MaxRentSuburb, ExtraArea, ExtraGarden)).

% compatible_house/2 
% Δέχεται ως είσοδο ένα request και ένα house και επιτυγχάνει όταν 
% το δοσμένο request είναι συμβατό με το συγκεκριμένο house.
compatible_house(house(_Addr,NBedrooms, Area, InCenter, Floor, Elevator, HPets, AreaGarden, Rent),
                 request(_Name, MinArea, MinBedroom, RPets, MinFloorElevator, MaxRent, MaxRentCenter, MaxRentSuburb, ExtraArea, ExtraGarden)) :-
    NBedrooms >= MinBedroom,
    Area >= MinArea,
    Rent =< MaxRent,
    % Η μόνη περίπτωση που δεν είναι συμβατά 
    % είναι αν ο αγοραστής θέλει κατοικίδια,
    % ενώ ο πωλητής όχι.
    \+ (HPets = no, RPets = yes),
    % Αν το διαμέρισμα είναι σε όροφο που ο αγοραστής 
    % θέλει να έχει ασανσέρ, τότε πρέπει να έχει.
    % Αν το διαμέρισμα είναι σε όροφο που ο αγοραστής δεν 
    % θέλει ασανσέρ, τότε δεν μας επηρεάζει αν έχει.
    (Floor < MinFloorElevator ; Elevator = yes),
    % Αυξήσεις λόγω επιπλέων τετραγωικών.
    IncreaseArea is (Area - MinArea)*ExtraArea,
    IncreaseGarden is AreaGarden*ExtraGarden,
    % Ελέγχουμε αν πληρούνται τα κριτήρια για το ενοίκιο
    % Υπολογίζουμε το ποσό το οποίο είναι διατεθημένος να πληρώσει
    % ο ενοικιαστής, κάνοντας προσαρμογή στα επιπλέον τετραγωνικά.
    % Αν το πραγματικό ενοίκιο είναι μικρότερο ή ίσο από την τιμή
    % τότε έχουμε πετύχει. Επίσης έχουμε διασφαλίσει από πρίν ότι
    % το ενοίκιο είναι σίγουρα μικρότερο από το μέγιστο ποσό που
    % θέλει να διαθέσει ο ενοικιαστής.
    (InCenter = yes -> !,MaxRentCenter+IncreaseArea+IncreaseGarden >= Rent;
     MaxRentSuburb+IncreaseArea+IncreaseGarden >= Rent).

% compatible_houses/3
% Δέχεται ως είσοδο:
% Το όνομα ενός αγοραστή
% Μία λίστα με τα διαθέσιμα σπίτια
% Μία λίστα στην οποία τοποθετούνται τα 
%  διαθέσιμα σπίτια που είναι συνμβατά
% Base case
compatible_houses(_X,[],[]).
% Περίπτωση επιτυχίας
compatible_houses(RName, [CurrentHouse|THouses], [CurrentHouse|TComp]) :-
    compatible_house(CurrentHouse, RName),
    % Αν είναι συμβατό, όρισε ως head των συμβατών σπιτιών 
    % το τρέχον σπίτι και συνέχισε την αναζήτηση στα υπόλοιπα σπίτια
    % με το tail των συμβατών.
    % Επίσης μην κάνεις οπισθοδρόμηση, αφού το σπίτι είναι συμβατό.
    !,
    compatible_houses(RName, THouses, TComp).
% Περίπτωση αποτυχίας
compatible_houses(RName, [_| THouses], Compatible) :-
    % Βγάλε το head και συνέχισε την αναζήτηση στα διαθέσιμα σπίτια.
    compatible_houses(RName, THouses, Compatible).

% find_min_by_index/3
% Δίνεται ως είσοδο μία λίστα με τα ονόματα των σπιτιών
% καθώς και το index του στοιχείου βάσει του
% οποίου θα κάνουμε sort στο house/9.
% Επιστρέφει τα αποτελέσματα στη λίστα MinList
find_min_by_index(Houses, Index, MinList) :-
    % Στην αρχή μετατρέπουμε τα ονόματα των 
    % σπιτιών στα αντίστοιχα γεγονότα.
    get_house_facts_from_names(Houses, HouseFacts),
    % Θεωρούμε το πρώτο στοιχείο ως το μικρότερο.
    [StartH|StartT] = HouseFacts,
    find_min_by_index(StartT, Index, MinListFacts, [StartH]),
    % Αντιστοίχησε τα facts σε ονόματα και τα βάζουμε στη MinList
    % την οποία επιστρέφουμε
    get_house_names_from_facts(MinListFacts, MinList).

% find_min_by_index/4
% Δέχεται ως είσοδο μία λίστα με τα ΓΕΓΟΝΟΤΑ των σπιτιών
% καθώς και το index του στοιχείου βάσει του
% οποίου θα κάνουμε sort στο house/9.
% Επιστρέφει τα αποτελέσματα στη MinList
% Η λίστά CurrentMin περιέχει όλα τα σπίτια με τη μικρότερη
% τιμή στο στοιχείο έως αυτή τη στιγμή.
find_min_by_index([H|T], Index, MinList, [MinH|_]) :-
    arg(Index, H, CurrentValue),
    arg(Index, MinH, CurrentMin),
    % Νέο min, διαγραφή της CurrentMinHouses και δημιουργία
    % νέας με το νέο min.
    CurrentValue < CurrentMin,
    find_min_by_index(T,Index,MinList,[H]).
find_min_by_index([H|T], Index, MinList, [MinH|MinTail]) :-
    arg(Index, H, CurrentValue),
    arg(Index, MinH, CurrentMin),
    % Ισότητα. Απλή προσθήκη στην CurrentMinHouses
    CurrentValue == CurrentMin,
    find_min_by_index(T,Index,MinList,[H,MinH|MinTail]).
find_min_by_index([H|T], Index, MinList, [MinH|MinTail]) :-
    arg(Index, H, CurrentValue),
    arg(Index, MinH, CurrentMin),
    % Μεγαλύτερο στοιχείο. Το αγοούμε
    CurrentValue > CurrentMin,
    find_min_by_index(T,Index,MinList,[MinH|MinTail]).
% Base case, έχουμε επεξεργαστεί όλα τα στοιχεία οπότε το
% currentminhouses είναι η τελική λίστα
find_min_by_index([],_, MinList, MinList).

% find_max_by_index_house_names/3
% Δίνεται ως είσοδο μία λίστα με τα ονόματα των σπιτιών
% καθώς και το index του στοιχείου βάσει του
% οποίου θα κάνουμε sort στο house/9.
% Επιστρέφει τα αποτελέσματα στη λίστα MaxList
% Ίδιο με το find_min_by_index
find_max_by_index_house_names(Houses, Index, MaxList) :-
    get_house_facts_from_names(Houses, HouseFacts),
    % Θεωρούμε το πρώτο στοιχείο ως το μικρότερο.
    [StartH|StartT] = HouseFacts,
    find_max_by_index(StartT, Index, MaxListFacts, [StartH]),
    % Αντιστοίχησε τα facts σε ονόματα
    get_house_names_from_facts(MaxListFacts, MaxList).

% Ίδιο με το παραπάνω αλλά δέχεται ως είσοδο facts όχι house names.
find_max_by_index_fact(Facts, Index, MaxList) :-
    [StartH|StartT] = Facts,
    find_max_by_index(StartT, Index, MaxList, [StartH]).

% find_max_by_index/4
% Οι κανόνες είναι ίδιοι με τους find_min_by_index
% με μια απλή αλλαγή στα πρόσημα των συγκρίσεων.
% Δέχεται ως είσοδο μία λίστα με τα ΓΕΓΟΝΟΤΑ των σπιτιών
% καθώς και το index του στοιχείου βάσει του
% οποίου θα κάνουμε sort στο house/9.
% Επιστρέφει τα αποτελέσματα στη MinList
% Η λίστά CurrentMax περιέχει όλα τα σπίτια με τη μεγαλύτερη
% τιμή στο στοιχείο έως αυτή τη στιγμή.
find_max_by_index([H|T], Index, MaxList, [MaxH|_]) :-
    arg(Index, H, CurrentValue),
    arg(Index, MaxH, CurrentMax),
    % Νέο max, διαγραφή της CurrentMaxHouses και δημιουργία
    % νέας με το νέο max.
    CurrentValue > CurrentMax,
    find_max_by_index(T,Index,MaxList,[H]).
find_max_by_index([H|T], Index, MaxList, [MaxH|MaxTail]) :-
    arg(Index, H, CurrentValue),
    arg(Index, MaxH, CurrentMax),
    % Ισότητα. Απλή προσθήκη στην CurrentMaxHouses
    CurrentValue == CurrentMax,
    find_max_by_index(T,Index,MaxList,[H,MaxH|MaxTail]).
find_max_by_index([H|T], Index, MaxList, [MaxH|MaxTail]) :-
    arg(Index, H, CurrentValue),
    arg(Index, MaxH, CurrentMax),
    % Μικρότερο στοιχείο. Το αγοούμε
    CurrentValue < CurrentMax,
    find_max_by_index(T,Index,MaxList,[MaxH|MaxTail]).
% Base case, έχουμε επεξεργαστεί όλα τα στοιχεία οπότε το
% currentmaxhouses είναι η τελική λίστα
find_max_by_index([],_, MaxList, MaxList).

% find_cheapest/2
% Δέχεται ως είσοδο μία λίστα από ονόματα σπιτιών
% και παράγει μία λίστα με αυτό/αυτά με το φθηνότερο
% ενοίκιο.
find_cheapest(Houses, Cheapest) :-
    % Το ενοίκιο βρίσκεται στο index 9
    find_min_by_index(Houses, 9, Cheapest).

% find_biggest_garden/2
% Δέχεται ως είσοδο μία λίστα από ονόματα σπιτιών
% και παράγει μία λίστα με αυτό/αυτά με το μεγαλύτερο
% κήπο
find_biggest_garden(Houses, BiggestGarden) :-
    % Το εμβαδό κήπου βρίσκεται στη θέση 8
    find_max_by_index_house_names(Houses, 8, BiggestGarden).

% find_biggest_garden/2
% Δέχεται ως είσοδο μία λίστα από ονόματα σπιτιών
% και παράγει μία λίστα με αυτό/αυτά με το μεγαλύτερο
% εμβαδό
find_biggest_house(Houses, BiggestHouses) :-
    % Το εμβαδό σπιτιού βρίσκεται στη θέση 3
    find_max_by_index_house_names(Houses, 3, BiggestHouses).

% find_best_houses/2
% Δέχεται ως είσοδο μία λίστα από ονόματα σπιτιών και
% επιστρέφει το καλύτερο με τον εξής τρόπο:
% Αν ένα διαμέρισμα είναι φθηνότερο, επιλέγεται αυτό.
% Αν υπάρχουν πολλά φθηνότερα επιλέγεται αυτό με το μεγαλύτερο κήπο.
% Αν ακόμα υπάρχουν πολλά επιλέγεται το μεγαλύτερο σε εμβαδό.
% Αν η λίστα με τα σπίτια είναι κενή τότε επιστρέφεται κενή λίστα.
find_best_houses([],[]).
find_best_houses(HouseList, BestHouseList) :-
    % Καλούμε όλες τις διαδικασίες, στην περίπτωση που 
    % βρεθεί ένα σπίτι με την πρώτη οι υπόλοιπες απλώς θα επιστρέψουν
    % την ίδια λίστα με αυτή που δώσαμε ως είσοδο.
    find_cheapest(HouseList, Cheapest),
    find_biggest_garden(Cheapest, BiggestGarden),
    find_biggest_house(BiggestGarden, BestHouseList).

% find_best_houses_from_list/2
% Δέχεται ως είσοδο μία λίστα με λίστες από σπίτια
% και επιστρέφει μία λίστα με λίστες με τα προτιμότερα 
% σπίτια από κάθε λίστα.
find_best_houses_from_list([],[]).
find_best_houses_from_list([AvailableHouses|RestAvailableHouses], [BestHouses|RestBestHouses]) :-
    find_best_houses(AvailableHouses, BestHouses),
    find_best_houses_from_list(RestAvailableHouses, RestBestHouses).

% find_houses/3 
% Δέχεται μία λίστα με requests, μία λίστα από διαθέσιμα σπίτια
% και επιστρέφει μία λίστα που περιέχει για κάθε request
% τα ονόματα των σπιτιών που είναι συνμβατά με το request.
find_houses([],_,[]).
find_houses([RenterH|RenterT], AvailableHouses, [CompatibleHead|CompatibleTail]) :-
    find_houses(RenterT, AvailableHouses, CompatibleTail),
    compatible_houses(RenterH, AvailableHouses, CompatibleHead).

% add_bidder/4
% Δέχεται ως είσοδο:
% όνομα ενός bidder
% όνομα του σπιτιού για το οποίο είναι υποψήφιος αγοραστής
% λίστα με τα ονόματα των σπιτιών
% λίστα με τα bids που ήδη υπάρχουν για αυτά τα σπίτια, στις ίδιες θέσεις με τα ονόματα
% επιστρέφει τη νέα λίστα με όλα τα bids, αφού προστεθεί το BName στη λίστα του HName
add_bidder(BName, HName, [HName|_], [BHead|BTail], [NewBhead|BTail]) :-
    % Έχουμε φτάσει στη θέση του στοιχείου HName
    % Τοποθετούμε στη λίστα με τους υποψήφιους bidders τον BName και
    % τοποθετούμε τη νέα λίστα στο output NewBHead.
    % Αφήνουμε τις υπόλοιπες λίστες με bids απείραχτες. (BTail)
    append(BHead, [BName], NewBhead).

add_bidder(BName, HName, [DiffHName|HTail], [BHead|BTail], [BHead|NewBTail]) :-
    HName \= DiffHName,
    % Δεν είμαστε στη σωστή θέση. Προσθέτουμε τη λίστα με τα bids
    % για αυτό το σπίτι στην έξοδο και προχωράμε.
    add_bidder(BName, HName, HTail, BTail, NewBTail).
% Δεν βρέθηκε το HName στη λίστα οπότε δεν δόθηκε σωστή λίστα.
add_bidder(_, _, [], _,_) :-
    fail.

% add_bidder_to_all_houses/5
% Δέχεται ως είσοδο:
% Το όνομα ενός bidder
% Μία λίστα με οπνόματα συμβατών σπιτιών
% Μία λίστα με όλα τα ονόματα των σπιτιών
% Μία λίστα με τα υπάρχοντα bids στις αντίστοιχες θέσεις με τα ονόματα
% Δίνει ως έξοδο τη λίστα με τα bids αφού έχουν προστεθεί όλα τα bids του BName για τα συμβατά σπίτια.
% Ουσιαστικά λέμε: πρόσθεσε τον bidder BName ο οποίος ενδιαφέρεται για όλα τα σπίτια στη λίστα CompatNames
% σε όλες τις λίστες των σπιτιών στη λίστα BidderListIn και δώσε τις νέες λίστες στη λίστα BidderListsOut.
% Το σπίτι του οποίου το όνομα βρίσκεται στη θέση i στη λίστα HNames έχει λίστα με bidders στη θέση i 
% στη λίστα BidderListIn
add_bidder_to_all_houses(BName, [CompatName|RestCompatNames], HNames,BidderListIn, BidderListOut) :-
    add_bidder(BName, CompatName,HNames, BidderListIn, BidderListNext),
    add_bidder_to_all_houses(BName, RestCompatNames, HNames, BidderListNext, BidderListOut).
% Όταν έχουν τελειώσει τα συμβατά σπίτια έχουν προστεθεί όλα τα bids του bname
add_bidder_to_all_houses(_,[],_,BidderListNext, BidderListNext).

% find_bidders/4
% Δέχεται ως είσοδο:
% Μία λίστα με ονόματα bidders και μία λίστα με τα ονόματα των συμβατών σπιτιών στις αντίστοιχες θέσειες.
% Έχει ως έξοδο μία λίστα με τα ονόματα των στπιτιών και μία λίστα με τα ονόματα των bidders για κάθε
% σπίτι στις αντίστοιχες θέσεις.
find_bidders(BidderNames, BidderCompatible, HouseNameListOut, BidderListOut) :-
    get_all_houses(HouseNameListOut),
    create_list_of_element(HouseNameListOut,[], BidderList),
    find_bidders(BidderNames, BidderCompatible, HouseNameListOut, BidderList, BidderListOut).

%find_bidders/5
% Δέχεται ως είσοδο:
% Μία λίστα με τα ονόματα των bidders
% Μία λίστα με τα σπίτια στα οποία κάνει bid ο κάθε bidder
% Μία λίστα με τα ονόματα των σπιτιών
% Μία λίστα με τους αρχικούς bidders για κάθε σπίτι
% Δίνει ως έξοδο τη νέα λίστα με τους bidders.
find_bidders([BidderName|RestBidders], [Compatible|RestCompatible], HouseNames, BidderListIn, BidderListOut) :-
    % Πρόσθεσε όλα τα bids του biddername
    add_bidder_to_all_houses(BidderName, Compatible, HouseNames, BidderListIn, BidderListNext),
    % Στην BidderListNext έχουν προστεθεί τα bids του BidderName
    find_bidders(RestBidders, RestCompatible, HouseNames, BidderListNext, BidderListOut).
% Στο τέλος έχουν προστεθεί τα bids όλων των bidders για όλα τα σπίτια
find_bidders([],[],_,BidderListNext,BidderListNext).

% calculate_bid/3
% Δέχεται το όνομα ενός σπιτιού και ενός αγοραστή και δίνει ως
% έξοδο το μέγιστο ποσό που διατίθεται να δώσει ο αγοραστής.
calculate_bid(HName, BName, Bid) :-
    house(HName,_, Area, InCenter, _, _, _, AreaGarden, _),
    request(BName, MinArea, _, _, _, _, MaxRentCenter, MaxRentSuburb, ExtraArea, ExtraGarden),
    % Όπως όταν ελέγχουμε το compatible house
    IncreaseArea is (Area - MinArea)*ExtraArea,
    IncreaseGarden is AreaGarden*ExtraGarden,
    (InCenter = yes -> !, Bid is MaxRentCenter+IncreaseArea+IncreaseGarden;
     Bid is MaxRentSuburb+IncreaseArea+IncreaseGarden).

% create_bids/3
% Έχοντας μία λίστα από υποψήφιους αγοραστές 
% και το όνομα του σπιτιού δημιουργεί 
% γεγονότα bid και τα τοποθετεί σε μία λίστα.
% Ουσιαστικά δημιουργεί bid για το σπίτι HName για κάθε αγοραστή στη 
% λίστα [BName|BTail] και τα βάζει στη λίστα [Bid|BidTail].
% Τα γεγονότα bid μας επιτρέπουν να χρησιμοποιήσουμε
% το find_max_by_index_fact που έχει ήδη υλοποιηθεί. 
create_bids(_,[],[]).
create_bids(HName, [BName|BTail], [Bid|BidTail]) :-
    create_bids(HName, BTail, BidTail),
    calculate_bid(HName, BName, BidValue),
    Bid = bid(BName, HName, BidValue).

% find_best_bidders/3
% Επιστρέφει στη λίστα BestBidders τους Bidders με
% την υψηλότερη προσφορά για το σπίτι.
find_best_bidders(HName, Bidders, BestBidders) :-
    create_bids(HName, Bidders, Bids),
    % Βρες τα max βάσει του Bidvalue
    find_max_by_index_fact(Bids,3, BestBidders).

% find_best_bidder/3
% Επιστρέφει στη λίστα BestBidders τον Bidder με
% την υψηλότερη προσφορά για το σπίτι.
find_best_bidder(HName, Bidders, BestBidder) :-
    create_bids(HName, Bidders, Bids),
    % Βρες τα max βάσει του Bidvalue
    find_max_by_index_fact(Bids,3, BestBidders),
    % Βρες τον πρώτο best bidder και πάρε το όνομά του
    [BestBidderFact|_] = BestBidders,
    arg(1,BestBidderFact, BestBidder).

% find_best_bidders_for_houses/3
% Δέχεται μία λίστα από σπίτια και μια λίστα από λίστες με
% bidders για αυτά τα σπίτια.
% Επιστρέφει λίστα με τον καλύτερο bidder για κάθε σπίτι (αυτόν που πληρώνει περισσότερα).
% Αν για ένα σπίτι δεν υπάρχει κανένας bidder τοποθετεί -1 στη λίστα.
find_best_bidders_for_houses([],[],[]).
% Το σπίτι δεν έχει υποψήφιους bidders, βάλε -1 στον bestbidder και προχώρα.
find_best_bidders_for_houses([_|RestHouses], [[]|RestHouseBidders], [-1| RestBestBidders]) :-
    find_best_bidders_for_houses(RestHouses, RestHouseBidders, RestBestBidders).
% Βρες τον best bidder για το σπίτι και αποθήκευσέ τον.
find_best_bidders_for_houses([House|RestHouses], [HouseBidders|RestHouseBidders], [BestBidder| RestBestBidders]) :-
    find_best_bidder(House, HouseBidders, BestBidder),
    find_best_bidders_for_houses(RestHouses, RestHouseBidders, RestBestBidders).

% merge_assignments/3
% Δίνεται μία λίστα με τις προηγούμενες αναθέσεις
% και μία λίστα με τις νέες αναθέσεις σπιτιών και συγχωνεύονται
% στην έξδοδο με τον εξής τρόπο: 
% Όπου στην πρώτη λίστα υπάρχει -1 και στην 2η υπάρχει 
% κάποια άλλη τιμή, τότε στην έξοδο τοποθετείται η τιμή της 2ης λίστας.
% Όπου υπάρχει -1 και στις 2 λίστες, τότε τοποθετείται -1.
% Βάζω θαυμαστικό στο τέλος γιατί κάθε φορά ισχύει 1 από τα 3.
merge_assignments([],[],[]).
merge_assignments([-1|OldT],[-1|NewT],[-1|MergedT]) :-
    merge_assignments(OldT,NewT,MergedT),!.
merge_assignments([-1|OldT],[NewH|NewT], [NewH|MergedT]) :-
    merge_assignments(OldT,NewT,MergedT),!.
merge_assignments([OldH|OldT],[-1|NewT], [OldH|MergedT]) :-
    merge_assignments(OldT,NewT,MergedT),!.
% Αν πάμε να κάνουμε ανάθεση σε κάποιο που έχει ήδη ανατεθεί, πετά error

% remove_house_from_compatible/3
% Αφαιρεί το σπίτι με το δοσμένο όνομα από όλες τις
% δοσμένες λίστες και επιστρέφει τις νέες λίστες.
remove_house_from_compatible(_,[],[]).
remove_house_from_compatible(HouseName, [CurrentCompatible|RestCompatible], [NewCompatible|RestNewCompatible]) :-
    delete(CurrentCompatible, HouseName, NewCompatible),
    remove_house_from_compatible(HouseName, RestCompatible, RestNewCompatible).

% remove_assigned_houses/5
% Αφαιρεί από τις λίστες μέσα στη λίστα Compatible τα σπίτια
% που έχουν ανατεθεί στη λίστα Assigned. Ένα σπίτι
% δεν έχει ανατεθεί αν υπάρχει -1 στην αντίστοιχη θέση στη λίστα
% assigned, αλλιώς υπάρχει το όνομα του αγοραστή.
% Επιπλέον, τοποθετεί κενές λίστες σε όσους έχουν ανατεθεί σπίτια, αφαιρώντας τους έτσι από τη διαδικασία.
% Στο τέλος έχουν αφαιρεθεί όλα όσα ανατέθηκαν
remove_assigned_houses([],[],_,Compatible, Compatible).
remove_assigned_houses([_|RestHouse], [-1|RestAssigned],Requests, Compatible, FinalCompatible) :-
    remove_assigned_houses(RestHouse, RestAssigned, Requests, Compatible, FinalCompatible).
remove_assigned_houses([CurrentHouse|RestHouse], [AssignedTo|RestAssigned], Requests, Compatible, FinalCompatible) :-
    % Έχουμε βρει σπίτι το οποίο έχει πουληθεί.
    % Βρες τη θέση του αγοραστή στη λίστα requests
    nth0(Index, Requests, AssignedTo),
    % Ο αγοραστής πλέον έχει κενή λίστα στα compatible σπίτια, αφού πλέον έχει ανατεθεί σπίτι σε αυτόν τον αγοραστή.
    replace_nth0(Compatible, Index, _, [], TrimmedCompatible),
    % Αφαίρεσε το σπίτι από όλες τις λίστες compatible.
    remove_house_from_compatible(CurrentHouse, TrimmedCompatible, RemovedCompatible),
    remove_assigned_houses(RestHouse, RestAssigned, Requests, RemovedCompatible, FinalCompatible).

% refine_houses/4
% Δέχεται μία λίστα από Requests, μία λίστα με τα συμβατά σπίτια
% με κάθε request.
% Επιστρέφει μία λίστα με το request που εξυπηρετείται από κάθε σπίτι.
% Αν ένα σπίτι δεν εξυπηρετεί κάποιο request, έχει την τιμή -1 στη
% λίστα FinalGivenOut.
refine_houses(Requests, Compatible, FinalGivenOut) :-
    get_all_houses(H),
    create_list_of_element(H, -1, GivenOutForNow),
    refine_houses(Requests, Compatible, GivenOutForNow, FinalGivenOut).

% Αν η λίστα compatible έχει μόνο άδειες λίστες τότε έχουν ανατεθεί σπίτια σε όλους όσους
% γινόταν.
refine_houses(_, Compatible, CurrentAssignments, CurrentAssignments) :-
    all_empty(Compatible).

refine_houses(Requests, Compatible, CurrentAssignments, FinalAssignments) :-
    % Αρχικά βρες από τα compatible κάθε request τα preferred.
    find_best_houses_from_list(Compatible, Preferred),
    % Βρες ποιοι είναι υποψήφιοι αγοραστές για το κάθε σπίτι.
    find_bidders(Requests, Preferred, HouseNames, Bidders),
    % Αν υπάρχουν συγκρούσεις διάλεξε αυτόν που δίνει τα περισσότερα.
    find_best_bidders_for_houses(HouseNames, Bidders, BestBidders),
    % Πρόσθεσε τα σπίτια που ανατέθηκαν μόλις, στη συνολική ανάθεση.
    merge_assignments(CurrentAssignments, BestBidders, NewAssignments),
    % Αφαίρεσε τα σπίτια που έχουν ανατεθεί από τις λίστες με τα compatible.
    remove_assigned_houses(HouseNames, NewAssignments,Requests, Compatible,NewCompatible),
    refine_houses(Requests, NewCompatible, NewAssignments, FinalAssignments).

% recommend_house/1
% Δέχεται μια λίστα με σπίτια και αν δεν είναι άδεια
% προτείνει ένα σπίτι για ενοικίαση.
% Αν η λίστα είναι άδεια μην κάνεις τίποτα
recommend_house([]).
recommend_house([H|T]) :-
    find_best_houses([H|T], BestHouses),
    print_recommended_houses(BestHouses),
    nl.

% display_compatible/2
% Δέχεται ως είσοδο μία λίστα με τα ονόματα των requests
% και μία λίστα που περιέχει στις αντοίστιχες θέσης λίστες
% με τα συμβατά διαμερίσματα του κάθε request και τα γράφει
% στην οθόνη.
display_compatible([RName|[]], [CompatibleH|[]]) :-
    % Τελευταίο request
    write("Κατάλληλα διαμερίσματα για τον πελάτη "), write(RName),write(":"),nl,
    write("====================================="),nl,
    print_houses_with_empty_message(CompatibleH),
    recommend_house(CompatibleH).

display_compatible([RName|RTail], [CompatibleH|CompatibleT]) :-
    write("Κατάλληλα διαμερίσματα για τον πελάτη "), write(RName),write(":"),nl,
    write("====================================="),nl,
    print_houses_with_empty_message(CompatibleH),
    recommend_house(CompatibleH),
    display_compatible(RTail, CompatibleT).

display_compatible([],[]) :-
    % Η λίστα δεν περιέχει καμία λίστα, άρα δεν υπάρχουν requests
    writeln("Δεν υπάρχουν requests στη βάση").

display_house_assignments(_,[],_).
display_house_assignments(Houses,[CurrentRequest|RestRequest],Assignments) :-
    % Το request αντιστοιχίστηκε σε κάποιο σπίτι
    % Βρες ποιο σπίτι ικανοποίησε το request
    % Αν αυτό κάνει fail, τότε το request δεν αντιστοιχίστηκε σε κάποιο σπίτι, οπότε πάμε στον κάτω ορισμό.
    nth0(Index, Assignments, CurrentRequest),!,
    nth0(Index, Houses, HouseName),
    write("O πελάτης "),write(CurrentRequest), write(" θα νοικιάσει το διαμέρισμα στην διεύθυνση: "), write(HouseName),nl,
    display_house_assignments(Houses, RestRequest, Assignments).
% Το request δεν αντιστοιχίστηκε σε κάποιο σπίτι
display_house_assignments(Houses,[CurrentRequest|RestRequest],Assignments) :-
    write("O πελάτης "),write(CurrentRequest), write("  δεν θα νοικιάσει κάποιο διαμέρισμα!"),nl,
    display_house_assignments(Houses, RestRequest, Assignments).

% process_menu_choice/1
% Εκτελεί τις κατάλληλες δράσεις ανάλογα με την επιλογή του χρήστη
process_menu_choice(1) :-
    % Δεν ζητάμε όνομα στην διαδραστική λειτουργία οπότε χρησιμοποιούμε
    % ένα τυχαίο UUID
    uuid(UUID),
    offer(UUID),
    get_all_houses(Houses),
    % Βρες ποια σπίτια είναι συμβατά
    compatible_houses(UUID, Houses, Compatible),
    nl,
    % Εκτύπωσε τα συμβατά σπίτια
    print_houses_with_empty_message(Compatible),
    % Βρες το προτεινόμενο σπίτι και εκτύπωσέ το.
    recommend_house(Compatible),
    % Διέγραψε την προσωρινή προσφορά
    delete_offer(UUID),
    % Ξαναδείξε το μενού.
    run.

process_menu_choice(2) :-
    % Βρίσκουμε τα ονόματα όλων των requests
    get_all_requests_names(Requests),
    get_all_houses(Houses),
    find_houses(Requests, Houses, Compatible),
    display_compatible(Requests, Compatible),
    run.

process_menu_choice(3) :-
    get_all_requests_names(Requests),
    get_all_houses(Houses),
    % Βρες τα αρχικά συμβατά σπίτια.
    find_houses(Requests, Houses, Compatible),
    % Βρές μία απόδοση σπιτιών
    refine_houses(Requests, Compatible, Assignments),
    display_house_assignments(Houses, Requests, Assignments),
    run.

% Στο 0 δεν κάνουμε τίποτα
process_menu_choice(0).

run :-
    print_menu,
    read_with_check(Ans,"Επιλογή: ", [0,1,2,3]),
    process_menu_choice(Ans). 
