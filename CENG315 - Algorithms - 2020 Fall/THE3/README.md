TAKE HOME EXAM 3 – RADIX SORT



int RadixSort (long arr[],bool ascending, int n, int l);

In this exam, you are asked to sort the given array $arr$ with Radix Sort ascending or descending depending on the boolean variable $ascending$ and return the number of iterations done in four loops of the Counting Sort algorithm (you need to use Counting Sort as a subroutine in the Radix Sort). $n$ is the number of elements. You are expected to use Counting Sort for $l$ digits at each time.



Constraints
Array size is either 1000 or 1000000. 

Array elements may take values up to numbers with 12 digits.

$l$ may take values 1,2,3,4 or 6.

IMPORTANT: Different than the algorithm in your book, initialize count array as int C[k] = {0} and use the fourth loop for copying the array back.