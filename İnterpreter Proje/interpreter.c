#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

#define MAX_VALUE_LEN 256
#define MAX_VAR_NAME_LEN 20
#define MAX_VARS 100
#define MAX_NUMBER_LEN 101  // +1 for '\0'
#define MAX_STRING_LEN 256

typedef enum {
    TOKEN_EOF, TOKEN_ERROR,
    TOKEN_NUMBER_KEYWORD, TOKEN_REPEAT, TOKEN_TIMES,
    TOKEN_WRITE, TOKEN_AND, TOKEN_NEWLINE,
    TOKEN_IDENTIFIER, TOKEN_INT_CONST, TOKEN_STRING_CONST,
    TOKEN_ASSIGN, TOKEN_INCREMENT, TOKEN_DECREMENT,
    TOKEN_SEMICOLON, TOKEN_OPEN_BLOCK, TOKEN_CLOSE_BLOCK
} TokenType;

typedef struct {
    TokenType type;
    char lexeme[256];
    int line, col;
} Token;

// Global Lexer State
FILE *source_file = NULL;
int line = 1, col = 0;
int last_char = ' ';

// --- Lexer Prototypes ---
Token get_next_token();
void error(const char* msg, int err_line, int err_col);

// --- Variables and Big Integer Representation ---

typedef struct {
    char name[MAX_VAR_NAME_LEN + 1];
    char value[MAX_NUMBER_LEN]; // number as string, supports up to 100 digits + sign
} Variable;

Variable variables[MAX_VARS];
int var_count = 0;

// Helper functions for big integer arithmetic

// Remove leading zeros from number string
void trim_leading_zeros(char *num) {
    int len = (int)strlen(num);
    int start = 0;
    int negative = 0;
    if (num[0] == '-') {
        negative = 1;
        start = 1;
    }
    while (num[start] == '0' && start < len - 1) start++;
    if (start > 0) {
        if (negative) {
            memmove(num + 1, num + start, len - start + 1);
        } else {
            memmove(num, num + start, len - start + 1);
        }
    }
    if (strcmp(num, "-0") == 0) {
        strcpy(num, "0");
    }
}

// Compare absolute values of num1 and num2 (both strings, no sign)
// returns 1 if num1 > num2, 0 if equal, -1 if num1 < num2
int abs_compare(const char* num1, const char* num2) {
    int len1 = (int)strlen(num1);
    int len2 = (int)strlen(num2);
    if (len1 > len2) return 1;
    if (len1 < len2) return -1;
    return strcmp(num1, num2);
}

// Add two positive big integers (strings), result in res (caller allocated)
void big_add_positive(const char* a, const char* b, char* res) {
    int la = (int)strlen(a);
    int lb = (int)strlen(b);
    int carry = 0, sum = 0;
    int i = la - 1, j = lb - 1, k = 0;
    char temp[MAX_NUMBER_LEN];
    while (i >= 0 || j >= 0 || carry) {
        int da = i >= 0 ? a[i] - '0' : 0;
        int db = j >= 0 ? b[j] - '0' : 0;
        sum = da + db + carry;
        carry = sum / 10;
        temp[k++] = (sum % 10) + '0';
        i--; j--;
    }
    // reverse temp to res
    for (int idx = 0; idx < k; idx++) {
        res[idx] = temp[k - 1 - idx];
    }
    res[k] = '\0';
}

// Subtract b from a, both positive big integers, assume a >= b
void big_sub_positive(const char* a, const char* b, char* res) {
    int la = (int)strlen(a);
    int lb = (int)strlen(b);
    int borrow = 0, diff;
    char temp[MAX_NUMBER_LEN];
    int i = la - 1, j = lb - 1, k = 0;
    while (i >= 0) {
        int da = a[i] - '0';
        int db = (j >= 0) ? b[j] - '0' : 0;
        diff = da - db - borrow;
        if (diff < 0) {
            diff += 10;
            borrow = 1;
        } else borrow = 0;
        temp[k++] = diff + '0';
        i--; j--;
    }
    // reverse temp and trim zeros
    int len = k;
    while (k > 1 && temp[k-1] == '0') k--;
    for (int idx = 0; idx < k; idx++) {
        res[idx] = temp[k - 1 - idx];
    }
    res[k] = '\0';
}

// Big integer addition (supports sign)
void big_add(const char* a, const char* b, char* res) {
    int sign_a = (a[0] == '-') ? -1 : 1;
    int sign_b = (b[0] == '-') ? -1 : 1;
    const char *num_a = (sign_a == -1) ? a + 1 : a;
    const char *num_b = (sign_b == -1) ? b + 1 : b;

    if (sign_a == sign_b) {
        big_add_positive(num_a, num_b, res);
        if (sign_a == -1 && strcmp(res, "0") != 0) {
            memmove(res + 1, res, strlen(res) + 1);
            res[0] = '-';
        }
    } else {
        int cmp = abs_compare(num_a, num_b);
        if (cmp == 0) {
            strcpy(res, "0");
        } else if (cmp > 0) {
            big_sub_positive(num_a, num_b, res);
            if (sign_a == -1) {
                memmove(res + 1, res, strlen(res) + 1);
                res[0] = '-';
            }
        } else {
            big_sub_positive(num_b, num_a, res);
            if (sign_b == -1) {
                memmove(res + 1, res, strlen(res) + 1);
                res[0] = '-';
            }
        }
    }
    trim_leading_zeros(res);
}

// Big integer subtraction a - b
void big_sub(const char* a, const char* b, char* res) {
    // a - b = a + (-b)
    char neg_b[MAX_NUMBER_LEN];
    if (b[0] == '-') {
        strcpy(neg_b, b + 1);
    } else {
        neg_b[0] = '-'; strcpy(neg_b + 1, b);
    }
    big_add(a, neg_b, res);
}

// Variable management
Variable* find_var(const char* name) {
    for (int i = 0; i < var_count; i++) {
        if (strcmp(variables[i].name, name) == 0) return &variables[i];
    }
    return NULL;
}

Variable* declare_var(const char* name) {
    if (strlen(name) > MAX_VAR_NAME_LEN) {
        fprintf(stderr, "Error: Variable name '%s' exceeds maximum length of %d characters.\n", name, MAX_VAR_NAME_LEN);
        exit(1);
    }
    if (var_count >= MAX_VARS) {
        fprintf(stderr, "Error: Variable limit exceeded.\n");
        exit(1);
    }
    if (find_var(name) != NULL) {
        fprintf(stderr, "Error: Variable '%s' already declared.\n", name);
        exit(1);
    }
    strcpy(variables[var_count].name, name);
    strcpy(variables[var_count].value, "0");  // initialize to zero
    return &variables[var_count++];
}


// Lexer functions

void next_char() {
    last_char = fgetc(source_file);
    if (last_char == '\n') {
        line++;
        col = 0;
    } else {
        col++;
    }
}

int is_identifier_start(int c) {
    return (isalpha(c));
}

int is_identifier_part(int c) {
    return (isalnum(c) || c == '_');
}

void skip_whitespace_and_comments() {
    while (1) {
        while (isspace(last_char)) next_char();

        if (last_char == '*') {
            // Skip comment
            next_char();
            int found_end = 0;
            while (last_char != EOF) {
                if (last_char == '*') {
                    next_char();
                    found_end = 1;
                    break;
                }
                next_char();
            }
            if (!found_end) {
                fprintf(stderr, "Error: unclosed comment at line %d col %d\n", line, col);
                exit(1);
            }
        } else {
            break;
        }
    }
}

Token make_token(TokenType type, const char* lexeme) {
    Token t;
    t.type = type;
    strncpy(t.lexeme, lexeme, sizeof(t.lexeme));
    t.lexeme[sizeof(t.lexeme) - 1] = 0;
    t.line = line;
    t.col = col;
    return t;
}

Token error_token(const char* msg) {
    Token t;
    t.type = TOKEN_ERROR;
    strncpy(t.lexeme, msg, sizeof(t.lexeme));
    t.lexeme[sizeof(t.lexeme) - 1] = 0;
    t.line = line;
    t.col = col;
    return t;
}

Token get_next_token() {
    skip_whitespace_and_comments();

    if (last_char == EOF) return make_token(TOKEN_EOF, "EOF");

    if (is_identifier_start(last_char)) {
       char buffer[256];
int i = 0;
while (is_identifier_part(last_char) && i < 255) {
    if (i >= MAX_VAR_NAME_LEN) {
        return error_token("Variable name too long (max 20 characters)");
    }
    buffer[i++] = last_char;
    next_char();
}
buffer[i] = 0;


        // Keywords
        if (strcmp(buffer, "number") == 0) return make_token(TOKEN_NUMBER_KEYWORD, buffer);
        if (strcmp(buffer, "repeat") == 0) return make_token(TOKEN_REPEAT, buffer);
        if (strcmp(buffer, "times") == 0) return make_token(TOKEN_TIMES, buffer);
        if (strcmp(buffer, "write") == 0) return make_token(TOKEN_WRITE, buffer);
        if (strcmp(buffer, "and") == 0) return make_token(TOKEN_AND, buffer);
        if (strcmp(buffer, "newline") == 0) return make_token(TOKEN_NEWLINE, buffer);

        // Otherwise identifier
        return make_token(TOKEN_IDENTIFIER, buffer);
    }

    // Numbers (positive only initially)
    if (isdigit(last_char)) {
        char buffer[256];
        int i = 0;
        
        while (isdigit(last_char) && i < 255) {
            buffer[i++] = last_char;
            next_char();
        }
        buffer[i] = 0;
        return make_token(TOKEN_INT_CONST, buffer);
    }

    // Handle minus sign (could be negative number or -= operator)
    if (last_char == '-') {
        // Peek ahead
        int pos = ftell(source_file);
        next_char();
        
        if (last_char == '=') {
            // It's -=
            next_char();
            return make_token(TOKEN_DECREMENT, "-=");
        } else if (isdigit(last_char)) {
            // It's a negative number
            char buffer[256];
            int i = 0;
            buffer[i++] = '-';
            
            while (isdigit(last_char) && i < 255) {
                buffer[i++] = last_char;
                next_char();
            }
            buffer[i] = 0;
            return make_token(TOKEN_INT_CONST, buffer);
        } else {
            // Just a minus sign (error in this language)
            return error_token("Unexpected character '-'");
        }
    }

    // Strings
    if (last_char == '"') {
        next_char();
        char buffer[256];
        int i = 0;
        while (last_char != '"' && last_char != EOF && i < 255) {
            if (last_char == '\\') {
                // Escape sequences \n \t \"
                next_char();
                if (last_char == 'n') buffer[i++] = '\n';
                else if (last_char == 't') buffer[i++] = '\t';
                else if (last_char == '"') buffer[i++] = '"';
                else buffer[i++] = last_char;
                next_char();
            } else {
                buffer[i++] = last_char;
                next_char();
            }
        }
        if (last_char != '"') {
            fprintf(stderr, "Error: unterminated string at line %d col %d\n", line, col);
            exit(1);
        }
        next_char();
        buffer[i] = 0;
        return make_token(TOKEN_STRING_CONST, buffer);
    }

    // Assignment operator
    if (last_char == ':') {
        next_char();
        if (last_char == '=') {
            next_char();
            return make_token(TOKEN_ASSIGN, ":=");
        }
        return error_token("Unexpected character ':'");
    }

    // Increment operator
    if (last_char == '+') {
        next_char();
        if (last_char == '=') {
            next_char();
            return make_token(TOKEN_INCREMENT, "+=");
        }
        return error_token("Unexpected character '+'");
    }

    if (last_char == ';') {
        next_char();
        return make_token(TOKEN_SEMICOLON, ";");
    }
    if (last_char == '{') {
        next_char();
        return make_token(TOKEN_OPEN_BLOCK, "{");
    }
    if (last_char == '}') {
        next_char();
        return make_token(TOKEN_CLOSE_BLOCK, "}");
    }

    // Unknown character
    char unknown[2] = { (char)last_char, 0 };
    next_char();
    return error_token(unknown);
}

// Parser and AST

typedef enum {
    NODE_PROGRAM,
    NODE_VAR_DECL,
    NODE_ASSIGN,
    NODE_INCREMENT,
    NODE_DECREMENT,
    NODE_WRITE,
    NODE_WRITE_LIST,
    NODE_WRITE_NEWLINE,
    NODE_REPEAT,
    NODE_BLOCK,
    NODE_INT_LITERAL,
    NODE_STRING_LITERAL,
    NODE_VAR_REFERENCE
} NodeType;

typedef struct ASTNode {
    NodeType type;
    int line, col;

    // For children or values
    union {
        // Var decl: var name
        char var_name[MAX_VAR_NAME_LEN + 1];

        // Assign: var name + expression
        struct {
            char var_name[MAX_VAR_NAME_LEN + 1];
            struct ASTNode *expr;
        } assign;

        // Increment / Decrement: var name + value
        struct {
            char var_name[MAX_VAR_NAME_LEN + 1];
            struct ASTNode *value;
        } incdec;

        // Write: list of expressions
        struct {
            struct ASTNode **items;
            int count;
        } write_list;

        // Repeat: count expression + body
        struct {
            struct ASTNode *count_expr;
            struct ASTNode *body;
        } repeat;

        // Block: list of statements
        struct {
            struct ASTNode **statements;
            int count;
        } block;

        // Int literal
        char int_val[MAX_NUMBER_LEN];

        // String literal
        char string_val[MAX_STRING_LEN];

        // Var reference
        char var_ref[MAX_VAR_NAME_LEN + 1];
    };
} ASTNode;

typedef struct {
    Token current_token;
} ParserState;

ParserState parser;

void advance();

int expect(TokenType t) {
    if (parser.current_token.type == t) {
        advance();
        return 1;
    }
    fprintf(stderr, "Parse error at line %d col %d: Expected token %d but got %d (%s)\n",
            parser.current_token.line, parser.current_token.col, t, parser.current_token.type, parser.current_token.lexeme);
    exit(1);
    return 0;
}

void advance() {
    parser.current_token = get_next_token();
    if (parser.current_token.type == TOKEN_ERROR) {
        fprintf(stderr, "Lexical error at line %d col %d: %s\n",
                parser.current_token.line, parser.current_token.col, parser.current_token.lexeme);
        exit(1);
    }
}

// Forward declarations
ASTNode* parse_program();
ASTNode* parse_statement();
ASTNode* parse_var_decl();
ASTNode* parse_assign();
ASTNode* parse_increment();
ASTNode* parse_decrement();
ASTNode* parse_write();
ASTNode* parse_repeat();
ASTNode* parse_block();
ASTNode* parse_expression();

ASTNode* new_node(NodeType type) {
    ASTNode* node = (ASTNode*)malloc(sizeof(ASTNode));
    if (!node) {
        fprintf(stderr, "Memory allocation failed\n");
        exit(1);
    }
    node->type = type;
    node->line = parser.current_token.line;
    node->col = parser.current_token.col;
    memset(&node->assign, 0, sizeof(node->assign));
    return node;
}

// program : { statement }
ASTNode* parse_program() {
    ASTNode* program = new_node(NODE_PROGRAM);
    program->block.statements = NULL;
    program->block.count = 0;

    while (parser.current_token.type != TOKEN_EOF) {
        ASTNode* stmt = parse_statement();
        program->block.statements = (ASTNode**)realloc(program->block.statements, sizeof(ASTNode*) * (program->block.count + 1));
        program->block.statements[program->block.count++] = stmt;
    }
    return program;
}

// statement : var_decl | assign | increment | decrement | write | repeat
ASTNode* parse_statement() {
    if (parser.current_token.type == TOKEN_NUMBER_KEYWORD) {
        return parse_var_decl();
    }
    if (parser.current_token.type == TOKEN_IDENTIFIER) {
        // Could be assign, increment, decrement
        Token t = parser.current_token;
        char var_name[MAX_VAR_NAME_LEN + 1];
        strncpy(var_name, t.lexeme, MAX_VAR_NAME_LEN);
        var_name[MAX_VAR_NAME_LEN] = 0;
        advance();

        if (parser.current_token.type == TOKEN_ASSIGN) {
            advance();
            ASTNode* expr = parse_expression();
            expect(TOKEN_SEMICOLON);

            ASTNode* assign_node = new_node(NODE_ASSIGN);
            strcpy(assign_node->assign.var_name, var_name);
            assign_node->assign.expr = expr;
            return assign_node;
        }
        else if (parser.current_token.type == TOKEN_INCREMENT) {
            advance();
            ASTNode* expr = parse_expression();
            expect(TOKEN_SEMICOLON);
            
            ASTNode* inc_node = new_node(NODE_INCREMENT);
            strcpy(inc_node->incdec.var_name, var_name);
            inc_node->incdec.value = expr;
            return inc_node;
        }
        else if (parser.current_token.type == TOKEN_DECREMENT) {
            advance();
            ASTNode* expr = parse_expression();
            expect(TOKEN_SEMICOLON);
            
            ASTNode* dec_node = new_node(NODE_DECREMENT);
            strcpy(dec_node->incdec.var_name, var_name);
            dec_node->incdec.value = expr;
            return dec_node;
        }
        else {
            fprintf(stderr, "Parse error at line %d col %d: Unexpected token '%s' after identifier\n",
                    parser.current_token.line, parser.current_token.col, parser.current_token.lexeme);
            exit(1);
        }
    }
    if (parser.current_token.type == TOKEN_WRITE) {
        return parse_write();
    }
    if (parser.current_token.type == TOKEN_REPEAT) {
        return parse_repeat();
    }

    fprintf(stderr, "Parse error at line %d col %d: Unexpected token '%s'\n",
            parser.current_token.line, parser.current_token.col, parser.current_token.lexeme);
    exit(1);
    return NULL;
}

// var_decl : "number" identifier ";"
ASTNode* parse_var_decl() {
    advance(); // consume 'number'
    if (parser.current_token.type != TOKEN_IDENTIFIER) {
        fprintf(stderr, "Parse error at line %d col %d: Expected identifier after 'number'\n",
                parser.current_token.line, parser.current_token.col);
        exit(1);
    }
    char var_name[MAX_VAR_NAME_LEN + 1];
    strncpy(var_name, parser.current_token.lexeme, MAX_VAR_NAME_LEN);
    var_name[MAX_VAR_NAME_LEN] = 0;
    advance();
    expect(TOKEN_SEMICOLON);

    ASTNode* node = new_node(NODE_VAR_DECL);
    strcpy(node->var_name, var_name);
    return node;
}

// expression : int_const | identifier
ASTNode* parse_expression() {
    if (parser.current_token.type == TOKEN_INT_CONST) {
        ASTNode* node = new_node(NODE_INT_LITERAL);
        strncpy(node->int_val, parser.current_token.lexeme, MAX_NUMBER_LEN - 1);
        node->int_val[MAX_NUMBER_LEN - 1] = 0;
        advance();
        return node;
    }
    if (parser.current_token.type == TOKEN_IDENTIFIER) {
        ASTNode* node = new_node(NODE_VAR_REFERENCE);
        strncpy(node->var_ref, parser.current_token.lexeme, MAX_VAR_NAME_LEN);
        node->var_ref[MAX_VAR_NAME_LEN] = 0;
        advance();
        return node;
    }
    fprintf(stderr, "Parse error at line %d col %d: Unexpected token in expression: %s\n",
            parser.current_token.line, parser.current_token.col, parser.current_token.lexeme);
    exit(1);
    return NULL;
}

// write : "write" output_list ";"
ASTNode* parse_write() {
    advance(); // consume 'write'
    
    ASTNode* write_node = new_node(NODE_WRITE_LIST);
    write_node->write_list.items = NULL;
    write_node->write_list.count = 0;
    
    // Parse first item
    ASTNode* item = NULL;
    if (parser.current_token.type == TOKEN_STRING_CONST) {
        item = new_node(NODE_STRING_LITERAL);
        strncpy(item->string_val, parser.current_token.lexeme, MAX_STRING_LEN - 1);
        item->string_val[MAX_STRING_LEN - 1] = 0;
        advance();
    } else if (parser.current_token.type == TOKEN_NEWLINE) {
        item = new_node(NODE_WRITE_NEWLINE);
        advance();
    } else {
        item = parse_expression();
    }
    
    // Add first item
    write_node->write_list.items = (ASTNode**)malloc(sizeof(ASTNode*));
    write_node->write_list.items[0] = item;
    write_node->write_list.count = 1;
    
    // Parse additional items with "and"
    while (parser.current_token.type == TOKEN_AND) {
        advance(); // consume 'and'
        
        ASTNode* next_item = NULL;
        if (parser.current_token.type == TOKEN_STRING_CONST) {
            next_item = new_node(NODE_STRING_LITERAL);
            strncpy(next_item->string_val, parser.current_token.lexeme, MAX_STRING_LEN - 1);
            next_item->string_val[MAX_STRING_LEN - 1] = 0;
            advance();
        } else if (parser.current_token.type == TOKEN_NEWLINE) {
            next_item = new_node(NODE_WRITE_NEWLINE);
            advance();
        } else {
            next_item = parse_expression();
        }
        
        // Add item to list
        write_node->write_list.items = (ASTNode**)realloc(write_node->write_list.items, 
                                                         sizeof(ASTNode*) * (write_node->write_list.count + 1));
        write_node->write_list.items[write_node->write_list.count] = next_item;
        write_node->write_list.count++;
    }
    
    expect(TOKEN_SEMICOLON);
    return write_node;
}

// repeat : "repeat" expression "times" (statement | block)
ASTNode* parse_repeat() {
    advance(); // consume 'repeat'
    ASTNode* count_expr = parse_expression();
    expect(TOKEN_TIMES);
    
    ASTNode* body = NULL;
    if (parser.current_token.type == TOKEN_OPEN_BLOCK) {
        body = parse_block();
    } else {
        body = parse_statement();
    }

    ASTNode* node = new_node(NODE_REPEAT);
    node->repeat.count_expr = count_expr;
    node->repeat.body = body;
    return node;
}

// block : "{" { statement } "}"
ASTNode* parse_block() {
    expect(TOKEN_OPEN_BLOCK);
    ASTNode* node = new_node(NODE_BLOCK);
    node->block.statements = NULL;
    node->block.count = 0;

    while (parser.current_token.type != TOKEN_CLOSE_BLOCK && parser.current_token.type != TOKEN_EOF) {
        ASTNode* stmt = parse_statement();
        node->block.statements = (ASTNode**)realloc(node->block.statements, sizeof(ASTNode*) * (node->block.count + 1));
        node->block.statements[node->block.count++] = stmt;
    }
    expect(TOKEN_CLOSE_BLOCK);
    return node;
}

// --- Interpreter ---

// Evaluate expression node to string value (number)
char* eval_expression(ASTNode* expr);

void runtime_error(const char* msg, int line, int col) {
    fprintf(stderr, "Runtime error at line %d col %d: %s\n", line, col, msg);
    exit(1);
}

char* eval_expression(ASTNode* expr) {
    static char temp_val[MAX_NUMBER_LEN];
    if (!expr) {
        runtime_error("Null expression", 0, 0);
    }
    switch (expr->type) {
        case NODE_INT_LITERAL:
            strncpy(temp_val, expr->int_val, MAX_NUMBER_LEN - 1);
            temp_val[MAX_NUMBER_LEN - 1] = 0;
            return temp_val;
        case NODE_VAR_REFERENCE: {
            Variable* v = find_var(expr->var_ref);
            if (!v) {
                runtime_error("Variable not declared", expr->line, expr->col);
            }
            return v->value;
        }
        default:
            runtime_error("Unsupported expression type", expr->line, expr->col);
    }
    return NULL;
}

void interpret_node(ASTNode* node);

void interpret_block(ASTNode* block) {
    for (int i = 0; i < block->block.count; i++) {
        interpret_node(block->block.statements[i]);
    }
}

void interpret_node(ASTNode* node) {
    if (!node) return;

    switch (node->type) {
        case NODE_PROGRAM:
            interpret_block(node);
            break;

        case NODE_VAR_DECL: {
            declare_var(node->var_name);
            break;
        }
        
        case NODE_ASSIGN: {
            Variable* v = find_var(node->assign.var_name);
            if (!v) {
                runtime_error("Assignment to undeclared variable", node->line, node->col);
            }
            char* val = eval_expression(node->assign.expr);
            strncpy(v->value, val, MAX_NUMBER_LEN - 1);
            v->value[MAX_NUMBER_LEN - 1] = 0;
            break;
        }
        
        case NODE_INCREMENT: {
            Variable* v = find_var(node->incdec.var_name);
            if (!v) {
                runtime_error("Increment of undeclared variable", node->line, node->col);
            }
            char* increment_val = eval_expression(node->incdec.value);
            char result[MAX_NUMBER_LEN];
            big_add(v->value, increment_val, result);
            strcpy(v->value, result);
            break;
        }
        
        case NODE_DECREMENT: {
            Variable* v = find_var(node->incdec.var_name);
            if (!v) {
                runtime_error("Decrement of undeclared variable", node->line, node->col);
            }
            char* decrement_val = eval_expression(node->incdec.value);
            char result[MAX_NUMBER_LEN];
            big_sub(v->value, decrement_val, result);
            strcpy(v->value, result);
            break;
        }
        
        case NODE_WRITE_LIST: {
            for (int i = 0; i < node->write_list.count; i++) {
                ASTNode* item = node->write_list.items[i];
                if (item->type == NODE_STRING_LITERAL) {
                    printf("%s", item->string_val);
                } else if (item->type == NODE_WRITE_NEWLINE) {
                    printf("\n");
                } else {
                    // Expression (number or variable)
                    char* val = eval_expression(item);
                    printf("%s", val);
                }
            }
            break;
        }
        
        case NODE_REPEAT: {
            char* count_str = eval_expression(node->repeat.count_expr);
            int count = atoi(count_str);
            
            // Handle negative counts
            if (count < 0) {
                count = 0;
            }
            
            // If count expression is a variable, we need to modify it during loop
            if (node->repeat.count_expr->type == NODE_VAR_REFERENCE) {
                Variable* loop_var = find_var(node->repeat.count_expr->var_ref);
                if (loop_var) {
                    // Store original value
                    char original_val[MAX_NUMBER_LEN];
                    strcpy(original_val, loop_var->value);
                    
                    // Loop while decrementing
                    for (int i = count; i > 0; i--) {
                        sprintf(loop_var->value, "%d", i);
                        interpret_node(node->repeat.body);
                    }
                    
                    // Set to 0 after loop
                    strcpy(loop_var->value, "0");
                } else {
                    // Simple loop with constant
                    for (int i = 0; i < count; i++) {
                        interpret_node(node->repeat.body);
                    }
                }
            } else {
                // Simple loop with constant
                for (int i = 0; i < count; i++) {
                    interpret_node(node->repeat.body);
                }
            }
            break;
        }
        
        case NODE_BLOCK:
            interpret_block(node);
            break;
            
        default:
            runtime_error("Unknown node type", node->line, node->col);
            break;
    }
}

// Free AST memory
void free_ast(ASTNode* node) {
    if (!node) return;
    
    switch (node->type) {
        case NODE_PROGRAM:
        case NODE_BLOCK:
            for (int i = 0; i < node->block.count; i++) {
                free_ast(node->block.statements[i]);
            }
            if (node->block.statements) {
                free(node->block.statements);
            }
            break;
            
        case NODE_ASSIGN:
            free_ast(node->assign.expr);
            break;
            
        case NODE_INCREMENT:
        case NODE_DECREMENT:
            free_ast(node->incdec.value);
            break;
            
        case NODE_WRITE_LIST:
            for (int i = 0; i < node->write_list.count; i++) {
                free_ast(node->write_list.items[i]);
            }
            if (node->write_list.items) {
                free(node->write_list.items);
            }
            break;
            
        case NODE_REPEAT:
            free_ast(node->repeat.count_expr);
            free_ast(node->repeat.body);
            break;
            
        default:
            // Leaf nodes, no children to free
            break;
    }
    
    free(node);
}

// Error handling
void error(const char* msg, int err_line, int err_col) {
    fprintf(stderr, "Error at line %d col %d: %s\n", err_line, err_col, msg);
    exit(1);
}

// Main function
int main(int argc, char* argv[]) {
    char filename[256];
    
    // Handle command line arguments
    if (argc > 1) {
        // Use provided filename
        strcpy(filename, argv[1]);
        // Add .ppp extension if not present
        if (strlen(filename) < 4 || strcmp(filename + strlen(filename) - 4, ".ppp") != 0) {
            strcat(filename, ".ppp");
        }
    } else {
        // Prompt for filename
        printf("Enter source file name: ");
        if (fgets(filename, sizeof(filename), stdin) == NULL) {
            fprintf(stderr, "Error reading filename\n");
            return 1;
        }
        
        // Remove newline
        int len = strlen(filename);
        if (len > 0 && filename[len-1] == '\n') {
            filename[len-1] = '\0';
        }
        
        // Add .ppp extension if not present
        if (strlen(filename) < 4 || strcmp(filename + strlen(filename) - 4, ".ppp") != 0) {
            strcat(filename, ".ppp");
        }
    }
    
    // Open source file
    source_file = fopen(filename, "r");
    if (!source_file) {
        fprintf(stderr, "Error: Cannot open file '%s'\n", filename);
        return 1;
    }
    
    // Initialize lexer
    next_char();
    
    // Initialize parser
    advance();
    
    // Parse program
    ASTNode* program = parse_program();
    
    // Close source file
    fclose(source_file);
    
    // Execute program
    interpret_node(program);
    
    // Cleanup
    free_ast(program);
    
    return 0;
}