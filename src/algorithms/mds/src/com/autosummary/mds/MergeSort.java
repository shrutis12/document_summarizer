package com.autosummary.mds;

/**
 * Created by Rakhi on 3/16/2017.
 */

public class MergeSort
{
    /* Merge Sort function */
    public static void sort(int[][] a, int low, int high)
    {
        int N = high - low;
        if (N <= 1)
            return;
        int mid = low + N/2;
        // recursively sort
        sort(a, low, mid);
        sort(a, mid, high);
        // merge two sorted subarrays
        int[][] temp = new int[N][2];
        int i = low, j = mid;
        for (int k = 0; k < N; k++) {
            if (i == mid) {
                temp[k][0] = a[j][0];
                temp[k][1] = a[j++][1];

            } else if (j == high) {
                temp[k][0] = a[i][0];
                temp[k][1] = a[i++][1];
            } else if (a[j][1] > a[i][1]) {
                temp[k][0] = a[j][0];
                temp[k][1] = a[j++][1];
            }
            else {
                temp[k][0] = a[i][0];
                temp[k][1] = a[i++][1];

            }
        }
        for (int k = 0; k < N; k++) {
            a[low + k][0] = temp[k][0];
            a[low + k][1] = temp[k][1];
        }
    }
    /* Main method */
    public static void main(int [][]arr,int n)
    {

        sort(arr, 0, n);

    }
}