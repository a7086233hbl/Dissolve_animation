package com.example.user.ui;

import android.annotation.TargetApi;
import android.os.Build;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by User on 2017/3/2.
 */
public class Shuffle_test {
        public static void main(String args[])
        {
            int[] solutionArray = { 1, 2, 3, 4, 5, 6, 16, 15, 14, 13, 12, 11 };

            shuffleArray(solutionArray);
            for (int i = 0; i < solutionArray.length; i++)
            {
                System.out.print(solutionArray[i] + " ");
            }
            System.out.println();
        }

        // Implementing Fisher–Yates shuffle
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        static void shuffleArray(int[] ar)
        {
            // If running on Java 6 or older, use `new Random()` on RHS here
            Random rnd = ThreadLocalRandom.current();
            for (int i = ar.length - 1; i > 0; i--)
            {
                int index = rnd.nextInt(i + 1);
                // Simple swap
                int a = ar[index];
                ar[index] = ar[i];
                ar[i] = a;
            }
        }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    static void shuffleArray2(int[][] ar)
    {
        // If running on Java 6 or older, use `new Random()` on RHS here
        Random rnd = ThreadLocalRandom.current();
        for (int i = ar.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            int[] a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }

}
