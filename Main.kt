package processor

import kotlin.math.floor

fun main() {
    while (true) {
        print("""
1. Add matrices
2. Multiply matrix by a constant
3. Multiply matrices
4. Transpose matrix
5. Calculate a determinant
6. Inverse matrix
0. Exit 
Your choice: 
        """.trimIndent())
        try {
            val cmd = readLine()!!.toInt()
            when (cmd) {
                0 -> break
                1 -> doAdd()
                2 -> doMulByConstant()
                3 -> doMulByMatrix()
                4 -> doTrans()
                5 -> doDeterminant()
                6 -> doInverse()
            }
        println()
        } catch (e: Exception) {
            println(e.message)
            println()
        }
    }
}

fun doInverse() {
    val m = readMatrix()
    printMatrix(invMatrix(m))
}

fun doDeterminant() {
    val m = readMatrix()
    val isDbl = m[0][0].toString().contains('.')
    val d = detMatrix(m)
    println("The result is:")
    println(if (isDbl) d else d.toInt())
}

fun doTrans() {
    print("""
1. Main diagonal
2. Side diagonal
3. Vertical line
4. Horizontal line 
Your choice: 
    """.trimIndent())
    val type = readLine()!!.toInt()
    val arr = readMatrix()
    printMatrix(transMatrix(arr, type))
}

fun doMulByConstant() {
    val arr = readMatrix()
    print("Enter constant: ")
    val k = readLine()!!.toDouble()
    printMatrix(scaleMatrix(arr, k))
}

fun doAdd() {
    val arr1 = readMatrix("first ")
    val arr2 = readMatrix("second ")
    printMatrix(addMatrix(arr1, arr2))
}

fun doMulByMatrix() {
    val arr1 = readMatrix("first ")
    val arr2 = readMatrix("second ")
    printMatrix(mulMatrix(arr1, arr2))
}

fun readMatrix(pref: String = ""): Array<DoubleArray> {
    print("Enter size of ${pref}matrix: ")
    val (rows, cols) = readLine()!!.split(" ").map { it.toInt() }
    val arr = Array(rows) { DoubleArray(cols) { 0.0 } }
    println("Enter ${pref}matrix:")
    for (r in 1..rows) {
        arr[r - 1] = readLine()!!.split(" ").map { it.toDouble() }.toDoubleArray()
    }
    return arr
}

fun addMatrix(m1: Array<DoubleArray>, m2: Array<DoubleArray>): Array<DoubleArray> {
    val r = m1.size
    val c = m1[0].size
    if (r != m2.size || c != m2[0].size)
        throw Exception("The operation cannot be performed.")
    val arr = Array(r) { DoubleArray(c) { 0.0 } }
    for (i in 0..r - 1) {
        for (j in 0..c - 1) {
            arr[i][j] = m1[i][j] + m2[i][j]
        }
    }
    return arr
}

fun mulMatrix(m1: Array<DoubleArray>, m2: Array<DoubleArray>): Array<DoubleArray> {
    val r1 = m1.size
    val c1 = m1[0].size
    val r2 = m2.size
    val c2 = m2[0].size
    if (c1 != r2)
        throw Exception("The operation cannot be performed.")
    val arr = Array(r1) { DoubleArray(c2) { 0.0 } }
    for (i in 0..r1 - 1) {
        for (j in 0..c2 - 1) {
            for (k in 0..c1 - 1) arr[i][j] += m1[i][k] * m2[k][j]
        }
    }
    return arr
}

fun scaleMatrix(m: Array<DoubleArray>, k: Double): Array<DoubleArray> {
    val r = m.size
    val c = m[0].size
    val arr = Array(r) { DoubleArray(c) { 0.0 } }
    for (i in 0..r - 1) {
        for (j in 0..c - 1) {
            arr[i][j] = k * m[i][j]
        }
    }
    return arr
}

fun transMatrix(m: Array<DoubleArray>, k: Int): Array<DoubleArray> {
    val r = m.lastIndex
    val c = m[0].lastIndex
    val iMax = if (k == 2) c else r
    val jMax = if (k == 2) r else c
    val arr = Array(iMax + 1) { DoubleArray(jMax + 1) { 0.0 } }
    for (i in 0..iMax) {
        for (j in 0..jMax) {
            arr[i][j] = when(k) {
                1 -> m[j][i]
                2 -> m[jMax - j][iMax - i]
                3 -> m[i][jMax - j]
                4 -> m[iMax - i][j]
                else -> m[i][j]
            }
        }
    }
    return arr
}

fun minor(m: Array<DoubleArray>, i0: Int, j0: Int): Array<DoubleArray> {
    val n = m.size
    val m2 = Array(n - 1) { DoubleArray(n - 1) { 0.0 } }
    var i2 = 0
    for (i in 1..n) {
        if (i != i0) {
            i2++
            var j2 = 0
            for (j in 1..n) {
                if (j != j0) {
                    j2++
                    m2[i2 - 1][j2 - 1] = m[i - 1][j - 1]
                }
            }
        }
    }
    return m2
}

fun detMatrix(m: Array<DoubleArray>): Double {
    val n = m.size
    if (n == 1) return m[0][0]
    if (n != m[0].size)
        throw Exception("The operation cannot be performed.")
    var det = 0.0
    val i = 1
    var k = 1.0
    for (j in 1..n) {
        det += m[i - 1][j - 1] * k * detMatrix(minor(m, i, j))
        k = -k
    }
    return det
}

fun invMatrix(m: Array<DoubleArray>): Array<DoubleArray> {
    val det = detMatrix(m)
    if (det == 0.0)
        throw Exception("This matrix doesn't have an inverse.")
    val n = m.size
    var cov = Array(n) { DoubleArray(n) { 0.0 } }
    for (i in 1..n) {
        for (j in 1..n) {
            val k = if((i + j) % 2 == 0) 1.0 else - 1.0
            cov[i - 1][j - 1] = k * detMatrix(minor(m, i, j))
        }
    }
    cov = transMatrix(cov, 1)
    return scaleMatrix(cov, 1 / det)
}

fun printMatrix(m: Array<DoubleArray>) {
    println("The result is:")
    val r = m.size
    val c = m[0].size
    for (i in 0..m.lastIndex) {
        for (j in 0..m[0].lastIndex) {
            val a = m[i][j]
            if (a == floor(a))
                print("${a.toInt()} ")
            else
                print("%.2f ".format(a))
        }
        println()
    }
}
