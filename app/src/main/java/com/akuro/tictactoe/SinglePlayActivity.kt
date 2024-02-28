package com.akuro.tictactoe

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.Timer
import kotlin.concurrent.timerTask

class SinglePlayActivity : AppCompatActivity() {
    private var player1Turn = true
    private var timeStart = false
    val board = Array(3){Array<TextView?>(3){null} }
    private lateinit var timing: Timer
    private lateinit var timer: TextView
    private lateinit var home: TextView
    private lateinit var restart: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_play)
        boardInit()
        timer = findViewById(R.id.time)
        home = findViewById(R.id.btn_home)
        restart = findViewById(R.id.btn_restart)

        home.setOnClickListener(){
            goHome();
        }

        restart.setOnClickListener(){
            reGame();
        }
    }

    private fun reGame(){
        finish()
        startActivity(intent)
    }

    private fun goHome(){
        finish()
    }

    private fun boardInit(){
        for (i in 0..2) { // Duyệt qua các hàng
            for (j in 0..2) { // Duyệt qua các cột
                val cellID = resources.getIdentifier("cell_${i}_$j", "id", packageName)
                board[i][j] = findViewById<TextView>(cellID).apply {
                    setOnClickListener {
                        onCellClicked(this, i, j)
                    }
                }
            }
        }
    }

    private fun onCellClicked(cell: TextView, row: Int, col: Int) {
        if (cell.text != "" || !player1Turn) return
        cell.text = if (player1Turn) "X" else "O"
        player1Turn = !player1Turn
        if (!player1Turn) {
            val handler = android.os.Handler(android.os.Looper.getMainLooper())
            handler.postDelayed({
                aiMakeMove()
            }, 500)
        }
        if(!timeStart){
            startTimer(timer);
            timeStart = true;
        }
        if (isGameOver(getBoardState()) == 1) {
            showOverDialog(1)
        }else if (isGameOver(getBoardState()) == 2){
            showOverDialog(2)
        }
    }

    private fun startTimer(tv_timer: TextView){
        var seconds = 0
        timing = Timer()
        timing.scheduleAtFixedRate(timerTask {
            seconds++
            val minutes = (seconds % 3600) / 60
            val secs = seconds % 60
            runOnUiThread {
                tv_timer.text = String.format("%02d:%02d", minutes, secs)
            } }, 0, 1000) // Lặp lại mỗi 1000 ms (1 giây)

    }

    private fun aiMakeMove() {
        if (!player1Turn) {
            val (bestRow, bestCol) = findBestMove(getBoardState())
            if (bestRow >= 0 && bestCol >= 0) {
                board[bestRow][bestCol]?.text = "O"
                player1Turn = !player1Turn
                if (isGameOver(getBoardState()) == 1) {
                    showOverDialog(1)
                }else if (isGameOver(getBoardState()) == 2){
                    showOverDialog(2)
                }
            }
        }
    }

    private fun showOverDialog(status: Int){
        timing.cancel()
        try {
            val builder = AlertDialog.Builder(this)
            builder.setCancelable(false)
            builder.setTitle("Game Over !!!")
            if(status == 1){
                builder.setMessage("Máy thắng !")
            }
            else{
                builder.setMessage("Huề !")
            }
            builder.setPositiveButton("OK"){ dialog, which -> reGame() }
            builder.show()
        } catch (ex:Exception){

        }
    }

    private fun getBoardState(): Array<Array<Char>> {
        return Array(3) { i ->
            Array(3) { j ->
                when (board[i][j]?.text.toString()) {
                    "X" -> 'X'
                    "O" -> 'O'
                    else -> ' '
                }
            }
        }
    }

    // Đánh giá trạng thái của bảng
    private fun evaluate(board: Array<Array<Char>>): Int {
        // Kiểm tra xem có dòng, cột hoặc đường chéo nào thắng không
        for (i in 0..2) {
            if (board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                if (board[i][0] == 'O') return +10
                if (board[i][0] == 'X') return -10
            }
            if (board[0][i] == board[1][i] && board[1][i] == board[2][i]) {
                if (board[0][i] == 'O') return +10
                if (board[0][i] == 'X') return -10
            }
        }
        // Kiểm tra đường chéo
        if (board[0][0] == board[1][1] && board[1][1] == board[2][2] || board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            if (board[1][1] == 'O') return +10
            if (board[1][1] == 'X') return -10
        }
        // Không ai thắng
        return 0
    }

    // Kiểm tra xem trò chơi có kết thúc không
    private fun isGameOver(board: Array<Array<Char>>): Int {
        if(evaluate(board) != 0) return 1
        else if(board.all { row -> row.all { cell -> cell != ' ' } }) return 2
        else return 0
    }

    // Giải thuật MiniMax
    private fun minimax(board: Array<Array<Char>>, depth: Int, isMax: Boolean): Int {
        val score = evaluate(board)

        if (score == 10 || score == -10) return score

        if (board.all { row -> row.all { cell -> cell != ' ' } }) return 0

        if (isMax) {
            var best = -1000
            for (i in 0..2) {
                for (j in 0..2) {
                    if (board[i][j] == ' ') {
                        board[i][j] = 'O'
                        best = maxOf(best, minimax(board, depth + 1, !isMax))
                        board[i][j] = ' '
                    }
                }
            }
            return best
        } else {
            var best = 1000
            for (i in 0..2) {
                for (j in 0..2) {
                    if (board[i][j] == ' ') {
                        board[i][j] = 'X'
                        best = minOf(best, minimax(board, depth + 1, !isMax))
                        board[i][j] = ' '
                    }
                }
            }
            return best
        }
    }

    // Tìm nước đi tốt nhất
    private fun findBestMove(board: Array<Array<Char>>): Pair<Int, Int> {
        var bestVal = -1000
        var bestMove = Pair(-1, -1)

        for (i in 0..2) {
            for (j in 0..2) {
                if (board[i][j] == ' ') {
                    board[i][j] = 'O'
                    val moveVal = minimax(board, 0, false)
                    board[i][j] = ' '

                    if (moveVal > bestVal) {
                        bestMove = Pair(i, j)
                        bestVal = moveVal
                    }
                }
            }
        }
        return bestMove
    }



}