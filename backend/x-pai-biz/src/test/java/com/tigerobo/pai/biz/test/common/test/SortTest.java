package com.tigerobo.pai.biz.test.common.test;

import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.util.Arrays;

public class SortTest {


    @Test
    public void heapSortTest() {

        int[] arr = new int[]{2, 3, 4, 22, 33, 21, 9};
        HeapSort heapSort = new HeapSort();
        int[] sort = heapSort.sort(arr);
        System.out.println(JSON.toJSONString(sort));
    }

    public class HeapSort {

        public int[] sort(int[] sourceArray) {
            // 对 arr 进行拷贝，不改变参数内容
            int[] arr = Arrays.copyOf(sourceArray, sourceArray.length);

            int len = arr.length;

            buildMaxHeap(arr, len);

            for (int i = len - 1; i > 0; i--) {
                swap(arr, 0, i);
                len--;
                heapify(arr, 0, len);
            }
            return arr;
        }

        private void buildMaxHeap(int[] arr, int len) {
            int floor = len / 2 - 1;
            for (int i = floor; i >= 0; i--) {
                heapify(arr, i, len);
            }
        }

        private void heapify(int[] arr, int i, int len) {
            int left = 2 * i + 1;
            int right = 2 * i + 2;
            int largest = i;

            if (left < len && arr[left] > arr[largest]) {
                largest = left;
            }

            if (right < len && arr[right] > arr[largest]) {
                largest = right;
            }

            if (largest != i) {
                swap(arr, i, largest);
                heapify(arr, largest, len);
            }
        }

        private void swap(int[] arr, int i, int j) {
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }

    }
}
