# BernsteinAlgorithm
Implementation of Bernstein algorithm for normalization of database with given functional dependencies.
# How  to run
To run program there are needed two arguments:
1. Path to source file with properly formatted functional dependencies.
2. Path to file where output (normalized schemas) will be saved. Path can point to a nonexistent file, but then included directory path must be valid.

Taking above points into consideration you can run program in command line like this:

`java -cp <path to class files> Main <source filepath> <output filepath>`

# How should look source file
1. Source file should be saved in plain text.
2. Each functional dependency must be saved in one separate line.
3. There are allowed empty lines - these will not count as dependencies or bad formatted entries.
4. 1. Dependency is built from left attributes, right attributes and dependency operand.
   2. Single Attribute is a mixture of letters A-Z, a-z, digits and underscores: `\w+` is regex for single attribute.
   3. Attributes sets are built from single attributes separated with commas. Spaces between attributes and commas are allowed. Attributes are closed in curly brackets. Regex for attributes looks like this: `\s*\{\s*(\w+\s*,\s*)*\w+\s*}\s*`
   4. Functional dependency is built from two sets of attributes separated with dependencies operand (`->`). Full regex for functional dependency looks like this: `^\s*\{\s*(\w+\s*,\s*)*\w+\s*}\s*->\s*\{\s*(\w+\s*,\s*)*\w+\s*}\s*$`

#### Example properly formatted dependencies:

```
{a,b,c,d,e}->{a}
{a}->{b}
{ab,12a}->{rs1,s2}
{ at1,  at }  -> { rs1, s2 }
{a1,b2} ->{a2, 22,  b3}
```
