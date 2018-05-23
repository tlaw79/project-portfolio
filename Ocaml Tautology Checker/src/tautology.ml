(* Tautology Checker *)
(* Tyler Law - 2017 *)

type formula = And of formula * formula
             | Or of formula * formula
             | Not of formula
             | Prop of string
             | True
             | False

type subst = (string * bool) list

exception KeepLooking

let show_list show l =
    let rec sl l =
        match l with
        | [] -> ""
        | [x] -> show x
        | x::xs -> show x ^ "; " ^ sl xs
    in "[ " ^ sl l ^ " ]"

let show_string_bool_pair (s, b) =
    "(\"" ^ s ^ "\"," ^ (if b then "true" else "false") ^ ")"

let show_subst = show_list show_string_bool_pair

let is_elem v l =
    List.fold_right (fun x in_rest -> if x = v then true else in_rest) l false

let rec explode = function
    | "" -> []
    | s -> String.get s 0 :: explode (String.sub s 1 ((String.length s) -1))

let dedup lst =
    let f elem to_keep =
        if is_elem elem to_keep then to_keep else elem::to_keep
    in List.fold_right f lst []

let rec getvalue id env =
    match env with
        | [] -> raise (Failure (id ^ " is not in scope."))
        | (x, value)::rest when x = id -> value
        | _::rest -> getvalue id rest

let rec eval (form : formula) (sub : subst) : bool =
    match form with
        | True -> true
        | False -> false
        | And (x, y) -> (match eval x sub, eval y sub with
                        | a, b -> a && b)
        | Or (x, y) -> (match eval x sub, eval y sub with
                        | a, b -> a || b)
        | Not (x) -> (match eval x sub with
                      | a -> not a)
        | Prop (s) -> getvalue s sub

let freevars (form : formula) : string list =
    let rec helper form =
        match form with
            | True -> []
            | False -> []
            | And (x, y) -> helper x @ helper y
            | Or (x, y) -> helper x @ helper y
            | Not (x) -> helper x
            | Prop (s) -> [s]
    in dedup (helper form)

let rec gen_subs lst sublst =
    match lst with
        | [] -> []
        | x::xs -> gen_subs xs sublst @
                   if (is_elem x sublst) then [(x, true)] else [(x, false)]

let is_tautology (form : formula) (fail : subst -> subst option) : subst option =
    let vars = freevars form in
        let rec try_subset partial_lst rest_lst =
            if not (eval form (gen_subs vars partial_lst)) && rest_lst = [] then
                fail (gen_subs vars partial_lst)
            else match rest_lst with
                 | [] -> raise KeepLooking
                 | x::xs -> try try_subset (partial_lst @ [x]) xs with
                            | KeepLooking -> try_subset partial_lst xs
        in try try_subset [] vars with
           | KeepLooking -> None

let is_tautology_print_all f =
    is_tautology f
        (fun s -> print_endline (show_subst s);
                  raise KeepLooking)

let is_tautology_first f = is_tautology f (fun s -> Some s)
