package gg.scala.commons.util

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.stream.Collectors

/**
 * @author AgentRKID
 * @since 8/7/2022
 */
object Files
{

    fun read(file: File, unit: (BufferedReader) -> Unit)
    {
        val reader = BufferedReader(FileReader(file))
        unit.invoke(reader)
    }

    fun write(str: String, file: File)
    {
        val writer = BufferedWriter(FileWriter(file))
        writer.write(str)
        writer.close()
    }

    fun copy(copy: File, copyTo: File)
    {
        read(copy) {
            write(it.lines().collect(Collectors.joining("\n")), copyTo)
        }
    }

}