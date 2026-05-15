package com.mahilashakti.unnati.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.mahilashakti.unnati.data.model.MemberEntity
import com.mahilashakti.unnati.data.model.SavingsEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class PdfGenerator(private val context: Context) {

    fun generateMemberPassbook(member: MemberEntity, savings: List<SavingsEntity>): File {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 Size
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()

        // Title
        paint.color = Color.BLACK
        paint.textSize = 24f
        paint.isFakeBoldText = true
        canvas.drawText("Member Passbook", 40f, 60f, paint)

        // Member Details
        paint.textSize = 14f
        paint.isFakeBoldText = false
        canvas.drawText("Name: ${member.name}", 40f, 100f, paint)
        canvas.drawText("Phone: ${member.phone}", 40f, 120f, paint)
        canvas.drawText("Date: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())}", 400f, 100f, paint)

        // Table Header
        paint.isFakeBoldText = true
        canvas.drawText("Date", 40f, 160f, paint)
        canvas.drawText("Amount", 150f, 160f, paint)
        canvas.drawText("Fine", 250f, 160f, paint)
        canvas.drawText("Total", 350f, 160f, paint)
        canvas.drawLine(40f, 170f, 555f, 170f, paint)

        // Table Content
        paint.isFakeBoldText = false
        var y = 190f
        val sdf = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
        savings.forEach { item ->
            canvas.drawText(sdf.format(Date(item.date)), 40f, y, paint)
            canvas.drawText("₹${item.amountPaid}", 150f, y, paint)
            canvas.drawText("₹${item.fineAmount}", 250f, y, paint)
            canvas.drawText("₹${item.amountPaid + item.fineAmount}", 350f, y, paint)
            y += 25f
            if (y > 800f) return@forEach // Basic overflow handling
        }

        pdfDocument.finishPage(page)

        val file = File(context.cacheDir, "reports/passbook_${member.id}.pdf")
        file.parentFile?.mkdirs()
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()
        return file
    }

    fun generateMonthlySummary(totalSavings: Double, totalLoans: Double, loanCount: Int): File {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()

        paint.textSize = 28f
        paint.isFakeBoldText = true
        canvas.drawText("Monthly SHG Summary", 40f, 70f, paint)

        paint.textSize = 18f
        paint.isFakeBoldText = false
        val dateStr = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())
        canvas.drawText("Report Period: $dateStr", 40f, 120f, paint)

        paint.textSize = 16f
        canvas.drawText("Total Group Savings: ₹$totalSavings", 40f, 180f, paint)
        canvas.drawText("Total Loans Disbursed: ₹$totalLoans", 40f, 210f, paint)
        canvas.drawText("Active Loan Count: $loanCount", 40f, 240f, paint)

        pdfDocument.finishPage(page)

        val file = File(context.cacheDir, "reports/shg_summary_${System.currentTimeMillis()}.pdf")
        file.parentFile?.mkdirs()
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()
        return file
    }
}
