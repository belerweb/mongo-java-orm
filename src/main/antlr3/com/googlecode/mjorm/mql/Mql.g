
grammar Mql;

options {
	output			= AST;
	ASTLabelType	= CommonTree;
	backtrack    = true;
}

tokens {
	QUESTION_MARK	  = '?';
	COLON		        = ':';
	SEMI_COLON    	= ';';
	STAR          	= '*';
	BACK_SLASH    	= '\\';
	FORWARD_SLASH 	= '/';
	EQUALS        	= '=';
	NOT_EQUALS    	= '!=';
	COMMA         	= ',';
	MINUS         	= '-';
	GT            	= '>';
	LT            	= '<';
	LT_GT		        = '<>';
	GT_EQUALS     	= '>=';
	LT_EQUALS     	= '<=';
	L_PAREN       	= '(';
	R_PAREN       	= ')';
	L_BRACKET     	= '[';
	R_BRACKET     	= ']';
	MATCHES       	= '=~';
	DOT           	= '.';
	TRUE          	= 'TRUE';
	FALSE         	= 'FALSE';
	FROM          	= 'FROM';
	WHERE         	= 'WHERE';
	SKIP          	= 'SKIP';
	LIMIT         	= 'LIMIT';
	NOT           	= 'NOT';
	SELECT          = 'SELECT';
	DELETE       	  = 'DELETE';
	UPDATE        	= 'UPDATE';
	EXPLAIN       	= 'EXPLAIN';
	HINT          	= 'HINT';
	NATURAL       	= 'NATURAL';
	ATOMIC        	= 'ATOMIC';
	INC           	= 'INC';
	UPSERT        	= 'UPSERT';
	MULTI         	= 'MULTI';
	UNSET         	= 'UNSET';
	SET           	= 'SET';
	POP           	= 'POP';
	SHIFT         	= 'SHIFT';
	PUSH          	= 'PUSH';
	EACH          	= 'EACH';
	PULL          	= 'PULL';
	RENAME        	= 'RENAME';
	BITWISE       	= 'BITWISE';
	SORT          	= 'SORT';
	ASC           	= 'ASC';
	DESC          	= 'DESC';
	RETURN        	= 'RETURN';
	NEW           	= 'NEW';
	OLD           	= 'OLD';
	OR            	= 'OR';
	AND           	= 'AND';
	ALL		          = 'ALL';
	
	FIND_AND_MODIFY	= 'FIND AND MODIFY';
	FIND_AND_DELETE	= 'FIND AND DELETE';
	ADD_TO_SET	    = 'ADD TO SET';

	COMMANDS;
	COMMAND;
	ACTION;
	
	ADD_TO_SET_EACH;
	PUSH_ALL;
	PULL_ALL;
	
	CRITERION;
	COMPARE_CRITERION;
	NEGATED_CRITERION;
	DOCUMENT_FUNCTION_CRITERION;
	FIELD_FUNCTION_CRITERION;
	
	CRITERIA;
	CRITERIA_GROUP;
	CRITERIA_GROUP_LIST;
	
	SELECT_ACTION;
	EXPLAIN_ACTION;
	UPDATE_ACTION;
	UPSERT_ACTION;
	FAM_ACTION;
	FAD_ACTION;
	DELETE_ACTION;
	
	FIELD_LIST;
	HINT_FIELD;
	SORT_FILED;
	UPDATE_OPERATIONS;
	
	ARRAY;
	PARAMETER;
	VARIABLE_LIST;
	FUNCTION_CALL;
}

@header {
	package com.googlecode.mjorm.mql;
}

@lexer::header {
	package com.googlecode.mjorm.mql;
}

/** start **/
start
	: c+=command (c+=command)* EOF -> ^(COMMANDS $c+)
	;

/** command **/
command
	: FROM collection_name (WHERE criteria)? action SEMI_COLON? -> ^(COMMAND collection_name criteria? action)
	;

/** criteria **/

criteria
	: c+=criterion (COMMA? c+=criterion)* -> ^(CRITERIA $c+)
	;

criteria_group
	: L_PAREN criteria R_PAREN -> ^(CRITERIA_GROUP criteria)
	;

criteria_group_list
	: c+=criteria_group (COMMA? c+=criteria_group)* -> ^(CRITERIA_GROUP_LIST $c+)
	;

criterion
	: (function_criterion | negated_field_criterion | field_criterion)
	;
	
field_criterion
	: (field_function_criterion | compare_criterion)
	;
		
negated_field_criterion
	: NOT field_criterion -> ^(NEGATED_CRITERION field_criterion)
	;
	
compare_criterion
	: field_name comparison_operator variable_literal -> ^(COMPARE_CRITERION field_name comparison_operator variable_literal)
	;
	
field_function_criterion
	: field_name function_call -> ^(FIELD_FUNCTION_CRITERION field_name function_call?)
	;

function_criterion 
	: function_call -> ^(DOCUMENT_FUNCTION_CRITERION function_call?)
	;

/** hint **/
hint
	: HINT NATURAL direction? -> ^(HINT NATURAL direction?)
	| HINT string direction? -> ^(HINT string direction?)
	| HINT f+=hint_field (COMMA? f+=hint_field)* -> ^(HINT $f+) 
	;

hint_field
	: field_name direction -> ^(HINT_FIELD field_name direction?)
	;

/** action **/
action
	: (select_action | explain_action | delete_action | update_action | fam_action | fad_action) -> ^(ACTION select_action? explain_action? delete_action? update_action? fam_action? fad_action?)
	;

// explain
explain_action
	: EXPLAIN hint? -> ^(EXPLAIN_ACTION hint?)
	;

// select
select_action
	: SELECT select_fields hint? sort_field_list? pagination? -> ^(SELECT_ACTION select_fields hint? sort_field_list? pagination?)
	;

select_fields 
	: STAR -> ^(FIELD_LIST STAR)
	| f+=field_name (COMMA? f+=field_name)* -> ^(FIELD_LIST $f+)
	;

pagination
 	: LIMIT (si=integer | sp=parameter) (COMMA (ei=integer | ep=parameter))? -> ^(LIMIT $si? $sp? $ei? $ep?)
 	;

// find and modify
fam_action
	: UPSERT? FIND_AND_MODIFY fam_return? update_operation_list SELECT select_fields sort_field_list? -> ^(FAM_ACTION UPSERT? fam_return? update_operation_list select_fields? sort_field_list?)
	;

fam_return
	: (RETURN^ (NEW | OLD))
	;
	
// find and delete
fad_action
	: FIND_AND_DELETE (SELECT select_fields)? sort_field_list? -> ^(FAD_ACTION select_fields? sort_field_list?)
	;

// delete
delete_action
	: ATOMIC? DELETE -> ^(DELETE_ACTION ATOMIC?)
	;

// update
update_action
	: ATOMIC? UPDATE MULTI? update_operation_list -> ^(UPDATE_ACTION ATOMIC? MULTI? update_operation_list)
	| ATOMIC? UPSERT update_operation_list -> ^(UPSERT_ACTION ATOMIC? update_operation_list)
	;
	
update_operation_list
	: u+=update_operation (COMMA? u+=update_operation)* -> ^(UPDATE_OPERATIONS $u+)
	;

update_operation
	: (
		operation_inc
		| operation_set 
		| operation_unset 
		| operation_push 
		| operation_push_all 
		| operation_add_to_set
		| operation_add_to_set_each
		| operation_pop
		| operation_shift
		| operation_pull
		| operation_pull_all
		| operation_rename
		| operation_bitwise
	)
	;

operation_inc
	: INC^ field_name number
	;
		
operation_set
	: SET^ field_name EQUALS! variable_literal
	;
	
operation_unset
	: UNSET^ field_name
	;
	
operation_push
	: PUSH^ field_name variable_literal
	;
			
operation_push_all
	: PUSH ALL field_name array -> ^(PUSH_ALL field_name array)
	;
			
operation_add_to_set_each
	: ADD_TO_SET field_name EACH array -> ^(ADD_TO_SET_EACH field_name array)
	;

operation_add_to_set
	: ADD_TO_SET^ field_name variable_literal
	;
		
operation_pop
	: POP^ field_name
	;
	
operation_shift
	: SHIFT^ field_name
	;
		
operation_pull
	: PULL^ field_name variable_literal
	;

operation_pull_all
	: PULL ALL field_name array -> ^(PULL_ALL field_name array)
	;

operation_rename
	: RENAME^ field_name field_name
	;

operation_bitwise
	: BITWISE^ (OR | AND) field_name INTEGER
	;
	
/** sort **/
sort_field_list
	: SORT s+=sort_field (COMMA? s+=sort_field)* -> ^(SORT $s+)
	;

sort_field
	: field_name direction? -> ^(SORT_FILED field_name direction?)
	;

/** general **/

collection_name
	: SCHEMA_IDENTIFIER
	;
		
field_name
	: SCHEMA_IDENTIFIER
	;

field_list
	: f=field_name (COMMA? f=field_name)* -> ^(FIELD_LIST $f+)
	;

function_name
	: SCHEMA_IDENTIFIER | ALL | OR | AND
	;

comparison_operator
	: (MATCHES | EQUALS | NOT_EQUALS | LT_GT | GT | LT | GT_EQUALS | LT_EQUALS)
	;

variable_literal
	: (parameter | regex | string | bool | number | array | variable_function_call)
	;

variable_list
	: v+=variable_literal (COMMA v+=variable_literal)* -> ^(VARIABLE_LIST $v+)
	;

function_call
	: function_name L_PAREN (criteria_group_list| criteria | variable_list)? R_PAREN -> ^(FUNCTION_CALL function_name criteria_group_list? criteria? variable_list?)
	;

variable_function_call
  : function_name L_PAREN variable_list? R_PAREN -> ^(FUNCTION_CALL function_name variable_list?)
  ;

integer
	: (SIGNED_INTEGER | INTEGER)
	;

decimal
	: (SIGNED_DECIMAL | DECIMAL)
	;

number
	: (HEX_NUMBER | integer | decimal)
	;
	
direction
	: (ASC | DESC)
	;

array
	: L_BRACKET variable_list? R_BRACKET -> ^(ARRAY variable_list)
	;

regex
	: REGEX
	;

string
	: (DOUBLE_QUOTED_STRING | SINGLE_QUOTED_STRING)
	;

bool
	: (TRUE | FALSE)
	;

parameter
	: (named_parameter | indexed_parameter) -> ^(PARAMETER named_parameter? indexed_parameter?)
	;

named_parameter
	: COLON SCHEMA_IDENTIFIER -> SCHEMA_IDENTIFIER
	;

indexed_parameter
	: QUESTION_MARK
	;

/**
 * LEXER RULES
 */

fragment HEX_DIGIT
	: ('0'..'9' | 'a'..'f' | 'A'..'F')
	;

fragment DIGIT
	: ('0'..'9')
	;

INTEGER
	: DIGIT+
	;

SIGNED_INTEGER
	: MINUS? DIGIT+
	;

HEX_NUMBER
	: '0' 'x' HEX_DIGIT+
	;

DECIMAL
	: INTEGER (DOT INTEGER)?
	;
	
SIGNED_DECIMAL
	: SIGNED_INTEGER (DOT INTEGER)?
	;
		
SCHEMA_IDENTIFIER
	: ('a'..'z' | 'A'..'Z' | '0'..'9' | '.' | '$' | '_' )+
	;

REGEX
  : FORWARD_SLASH (ESCAPE | ~(BACK_SLASH | FORWARD_SLASH))* FORWARD_SLASH
  ;
  
DOUBLE_QUOTED_STRING
    :   '"' (ESCAPE | ~( '\\' | '"' ))* '"' 
    ;
  
SINGLE_QUOTED_STRING
    :   '\'' (ESCAPE | ~( '\\' | '\'' ))* '\'' 
    ;

WHITESPACE
	: ( '\t' | ' ' | '\r' | '\n' )+ {skip();}
	;

fragment ESCAPE
  : '\\' ( 'N' | 'R' | 'T' | 'B' | 'F' | '"' | '\'' | '\\')
  ;
