package Sorting_class;

public class QuickSort {
    public static void quickSort(int[] arr) {
        // original: 2 8 3 [5] 6
        // partioned: 2 3 [5] 6 8
        // Base case: array of length <= 1 is already sorted
        if (arr.length <= 1) return;

        // 1. Pick a pivot (the middle element)
        /* TODO */
        int pivot = arr[arr.length / 2];

        // 2. Count how many elements are strictly smaller than the pivot
        int n_smaller_than_pivot = 0;
        int n_equal_to_pivot = 0;
        // TODO: loop through arr and count
        for (int i=0; i < arr.length; i++) {
            if (arr[i] < pivot) {
                n_smaller_than_pivot++;
            }
            if (arr[i] == pivot) {
                n_equal_to_pivot++;
            }
        }
        // 3. Allocate two arrays:
        //      arr1 holds elements < pivot
        //      arr2 holds elements > pivot
        //    (The pivot itself goes in the gap between them.)
        int[] arr1 = new int[n_smaller_than_pivot];
        int[] arr2 = new int[arr.length - n_smaller_than_pivot - n_equal_to_pivot];

        // 4. Fill arr1 and arr2 by scanning arr once
        int pos1 = 0;
        int pos2 = 0;
        // TODO
        for (int i=0; i < arr.length; i++) {
            if (arr[i] < pivot) {
                arr1[pos1] = arr[i];
                pos1++;
            } else if (arr[i] > pivot) {
                arr2[pos2] = arr[i];
                pos2++;
            }
        }

        // 5. Recursively sort arr1 and arr2
        // TODO
        quickSort(arr1);
        quickSort(arr2);

        // 6. Stitch the result back into arr:
        // arr  =  [ arr1 sorted ] [ pivot ] [ arr2 sorted ]
        for (int i=0; i < arr.length; i++) {
            if (i < arr1.length) {
                arr[i] = arr1[i];
            } else if (i >= arr1.length && i < arr1.length + n_equal_to_pivot) {
                arr[i] = pivot;
            }
            else {
                arr[i] = arr2[i - arr1.length - n_equal_to_pivot];
            }
        }
    }

    public static void main(String[] args) {
        int[][] tests = {
            {5, 4, 6, 3, 7, 2, 2, 2, 1, 8,},
            // {3, 1, 4, 5, 9, 2, 6},
        };
        for (int[] arr : tests) {
            int[] in = arr.clone();
            quickSort(arr);
            System.out.println("in:  " + java.util.Arrays.toString(in));
            System.out.println("out: " + java.util.Arrays.toString(arr));
            System.out.println();
        }
    }
}
