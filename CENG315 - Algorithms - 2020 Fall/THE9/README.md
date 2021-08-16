### TAKE HOME EXAM 9 – Sting Matching using FSA

```char* search(char txt[], char pattern[],char alphabet[])```
We would like to search a given pattern ```P``` on a given text ```T``` with the following features:

- Only capital letters are used in the alphabet. The alphabet is going to be given to you as 2 letters, the first and the last letter that will be used.

- The lengths of the pattern and the text is limited as follows: |P|<20 and |T|<2000.

- Wild character is available that can match with any character. The wild character can occur only in the pattern and it is represented by "?".

- There can be at most one wild character in the pattern.

Your task is to construct the FSA  transition table for the given problem instance and determine the starting indexes of each occurrence of the pattern in the text (the starting index of the text is 1).

Since FSA construction is the prepossessing phase of the whole search problem, your FSA construction implementation does not have to be the most efficient one. However, the search operation should still be in linear time.

*Hint*: For the version with wild character ```(?)``` the simplest solution acceptable is as follows: construct as many FSA as the number of characters in the alphabet by replacing the ```?``` in each one with these characters. During the search phase, you should execute each FSA simultaneously and report the matching obtained from each one.

Input
```
L_1, L_k : first and the last letter of the alphabet to be used in the text and the pattern
XX...XX  : the pattern with m characters (may include at most 1 wild character '?')
YY...YY  : the text with n characters
```
Output
```
If there is a wildcard in the pattern:
    I_1 I_2 ... I_t : starting indexes for the occurrences of the pattern P on the text T(must be sorted).
Else:
    Q_1 Q_2 ... Q_k : transitions to states from 0:  to Q_1 for the character L_1 as the input, ..., to Q_k for the input L_k
    ...             : m lines for all transitions (states are integers separated by a blank,note that there will be m+1 lines regarding FSA with the above one)
    I_1 I_2 ... I_t : starting indexes for the occurrences of the pattern P on the text T(must be sorted).
```