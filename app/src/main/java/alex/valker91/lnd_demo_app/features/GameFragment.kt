package alex.valker91.lnd_demo_app.features

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import alex.valker91.lnd_demo_app.databinding.FragmentGameBinding
import kotlin.math.abs

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    private val board = Array(4) { IntArray(4) }
    private var score = 0
    private var bestScore = 0
    private var gameOver = false
    private var won = false
    private var isAnimating = false

    private val tileViews = Array(4) { arrayOfNulls<TileView>(4) }

    private data class TileView(val frame: FrameLayout, val image: ImageView, val label: TextView)
    private data class TileMove(val fromRow: Int, val fromCol: Int, val toRow: Int, val toCol: Int)
    private data class RowMove(val fromIdx: Int, val toIdx: Int)

    private enum class Direction { LEFT, RIGHT, UP, DOWN }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        if (savedInstanceState != null) {
            restoreState(savedInstanceState)
        }

        setupGrid()
        setupGestures()

        binding.btnRestart.setOnClickListener { startGame() }

        if (savedInstanceState == null) {
            startGame()
        } else {
            renderBoard()
            updateScoreUi()
        }
    }

    private fun setupGrid() {
        val gridLayout = GridLayout(requireContext()).apply {
            rowCount = 4
            columnCount = 4
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        for (row in 0..3) {
            for (col in 0..3) {
                val frame = FrameLayout(requireContext()).apply {
                    setBackgroundColor(Color.parseColor("#5D5A50"))
                }

                val imageView = ImageView(requireContext()).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }

                val label = TextView(requireContext()).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                    gravity = android.view.Gravity.CENTER
                    textSize = 18f
                    setTextColor(Color.WHITE)
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                    setShadowLayer(2f, 1f, 1f, Color.BLACK)
                }

                frame.addView(imageView)
                frame.addView(label)

                val params = GridLayout.LayoutParams(
                    GridLayout.spec(row, 1f),
                    GridLayout.spec(col, 1f)
                ).apply {
                    setMargins(4, 4, 4, 4)
                    width = 0
                    height = 0
                }
                frame.layoutParams = params

                gridLayout.addView(frame)
                tileViews[row][col] = TileView(frame, imageView, label)
            }
        }

        binding.gameBoard.addView(gridLayout)
    }

    private fun setupGestures() {
        val detector = GestureDetectorCompat(requireContext(),
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onDown(e: MotionEvent) = true

                override fun onFling(
                    e1: MotionEvent?,
                    e2: MotionEvent,
                    velocityX: Float,
                    velocityY: Float
                ): Boolean {
                    val dx = e2.x - (e1?.x ?: 0f)
                    val dy = e2.y - (e1?.y ?: 0f)
                    val minSwipe = 50f
                    if (abs(dx) > abs(dy)) {
                        if (abs(dx) > minSwipe) {
                            handleMove(if (dx > 0) Direction.RIGHT else Direction.LEFT)
                            return true
                        }
                    } else {
                        if (abs(dy) > minSwipe) {
                            handleMove(if (dy > 0) Direction.DOWN else Direction.UP)
                            return true
                        }
                    }
                    return false
                }
            })

        binding.gameBoard.setOnTouchListener { _, event ->
            detector.onTouchEvent(event)
            true
        }
    }

    private fun startGame() {
        isAnimating = false
        for (row in 0..3) board[row].fill(0)
        score = 0
        gameOver = false
        won = false
        binding.tvGameStatus.visibility = View.GONE
        addRandomTile()
        addRandomTile()
        renderBoard()
        updateScoreUi()
    }

    private fun addRandomTile(): Pair<Int, Int>? {
        val empty = mutableListOf<Pair<Int, Int>>()
        for (r in 0..3) for (c in 0..3) if (board[r][c] == 0) empty.add(r to c)
        if (empty.isEmpty()) return null
        val (r, c) = empty.random()
        board[r][c] = if (Math.random() < 0.9) 2 else 4
        return r to c
    }

    private fun handleMove(dir: Direction) {
        if (gameOver || isAnimating) return

        val (moved, moves) = when (dir) {
            Direction.LEFT -> moveLeft()
            Direction.RIGHT -> moveRight()
            Direction.UP -> moveUp()
            Direction.DOWN -> moveDown()
        }

        if (moved) {
            isAnimating = true
            val newTile = addRandomTile()
            animateMoves(moves, newTile) {
                isAnimating = false
                updateScoreUi()
                if (!won && board.any { row -> row.any { it == 2048 } }) {
                    won = true
                    showStatus("You Win!")
                } else if (checkGameOver()) {
                    gameOver = true
                    showStatus("Game Over")
                }
            }
        }
    }

    // Returns (newRow, scoreGain, moves) where each RowMove is (fromIndex -> toIndex)
    private fun mergeRowTracked(row: IntArray): Triple<IntArray, Int, List<RowMove>> {
        var gain = 0
        val tiles = mutableListOf<Pair<Int, Int>>() // (value, originalIndex)
        for (i in row.indices) if (row[i] != 0) tiles.add(row[i] to i)

        val result = IntArray(4)
        val moves = mutableListOf<RowMove>()
        var dest = 0
        var i = 0
        while (i < tiles.size) {
            if (i + 1 < tiles.size && tiles[i].first == tiles[i + 1].first) {
                result[dest] = tiles[i].first * 2
                gain += result[dest]
                moves.add(RowMove(tiles[i].second, dest))
                moves.add(RowMove(tiles[i + 1].second, dest))
                dest++; i += 2
            } else {
                result[dest] = tiles[i].first
                moves.add(RowMove(tiles[i].second, dest))
                dest++; i++
            }
        }
        return Triple(result, gain, moves)
    }

    private fun moveLeft(): Pair<Boolean, List<TileMove>> {
        var moved = false
        val allMoves = mutableListOf<TileMove>()
        for (r in 0..3) {
            val (merged, gain, rowMoves) = mergeRowTracked(board[r])
            if (!merged.contentEquals(board[r])) moved = true
            board[r] = merged
            score += gain
            rowMoves.forEach { allMoves.add(TileMove(r, it.fromIdx, r, it.toIdx)) }
        }
        return moved to allMoves
    }

    private fun moveRight(): Pair<Boolean, List<TileMove>> {
        var moved = false
        val allMoves = mutableListOf<TileMove>()
        for (r in 0..3) {
            val reversed = board[r].reversed().toIntArray()
            val (merged, gain, rowMoves) = mergeRowTracked(reversed)
            val result = merged.reversed().toIntArray()
            if (!result.contentEquals(board[r])) moved = true
            board[r] = result
            score += gain
            rowMoves.forEach { allMoves.add(TileMove(r, 3 - it.fromIdx, r, 3 - it.toIdx)) }
        }
        return moved to allMoves
    }

    private fun moveUp(): Pair<Boolean, List<TileMove>> {
        val t = transpose()
        var moved = false
        val allMoves = mutableListOf<TileMove>()
        for (c in 0..3) {
            val (merged, gain, rowMoves) = mergeRowTracked(t[c])
            if (!merged.contentEquals(t[c])) moved = true
            t[c] = merged
            score += gain
            rowMoves.forEach { allMoves.add(TileMove(it.fromIdx, c, it.toIdx, c)) }
        }
        applyTransposed(t)
        return moved to allMoves
    }

    private fun moveDown(): Pair<Boolean, List<TileMove>> {
        val t = transpose()
        var moved = false
        val allMoves = mutableListOf<TileMove>()
        for (c in 0..3) {
            val reversed = t[c].reversed().toIntArray()
            val (merged, gain, rowMoves) = mergeRowTracked(reversed)
            val result = merged.reversed().toIntArray()
            if (!result.contentEquals(t[c])) moved = true
            t[c] = result
            score += gain
            rowMoves.forEach { allMoves.add(TileMove(3 - it.fromIdx, c, 3 - it.toIdx, c)) }
        }
        applyTransposed(t)
        return moved to allMoves
    }

    private fun transpose(): Array<IntArray> {
        val t = Array(4) { IntArray(4) }
        for (r in 0..3) for (c in 0..3) t[c][r] = board[r][c]
        return t
    }

    private fun applyTransposed(t: Array<IntArray>) {
        for (r in 0..3) for (c in 0..3) board[r][c] = t[c][r]
    }

    private fun animateMoves(moves: List<TileMove>, newTile: Pair<Int, Int>?, onComplete: () -> Unit) {
        // Reset all transforms before rendering new board state
        for (r in 0..3) for (c in 0..3) {
            tileViews[r][c]?.frame?.apply {
                translationX = 0f; translationY = 0f; scaleX = 1f; scaleY = 1f
            }
        }
        renderBoard()

        // Hide new tile — it will pop in after slide animation
        val newTileFrame = newTile?.let { (r, c) -> tileViews[r][c]?.frame }
        newTileFrame?.let { it.scaleX = 0f; it.scaleY = 0f }

        // Group moves by destination to detect merges
        val byDest = moves.groupBy { it.toRow to it.toCol }

        data class AnimInfo(val frame: View, val dx: Float, val dy: Float, val isMerge: Boolean)

        // Compute all offsets BEFORE applying any translations (getLocationOnScreen is position-sensitive)
        val animInfos = mutableListOf<AnimInfo>()
        for ((destKey, destMoves) in byDest) {
            val (toRow, toCol) = destKey
            val destFrame = tileViews[toRow][toCol]?.frame ?: continue

            // Pick the tile that travels the farthest as the visual representative
            val primary = destMoves.maxByOrNull {
                abs(it.fromRow - it.toRow) + abs(it.fromCol - it.toCol)
            } ?: continue
            if (primary.fromRow == primary.toRow && primary.fromCol == primary.toCol) continue

            val srcFrame = tileViews[primary.fromRow][primary.fromCol]?.frame ?: continue
            val srcLoc = IntArray(2); srcFrame.getLocationOnScreen(srcLoc)
            val dstLoc = IntArray(2); destFrame.getLocationOnScreen(dstLoc)

            animInfos.add(AnimInfo(
                destFrame,
                (srcLoc[0] - dstLoc[0]).toFloat(),
                (srcLoc[1] - dstLoc[1]).toFloat(),
                destMoves.size > 1
            ))
        }

        if (animInfos.isEmpty()) {
            animateNewTile(newTileFrame, onComplete)
            return
        }

        // Apply initial translations so tiles appear at their source positions
        for (info in animInfos) {
            info.frame.translationX = info.dx
            info.frame.translationY = info.dy
        }

        val slideDuration = 150L
        val slideAnimators = animInfos.flatMap { info ->
            listOf(
                ObjectAnimator.ofFloat(info.frame, View.TRANSLATION_X, info.dx, 0f).apply {
                    duration = slideDuration
                    interpolator = DecelerateInterpolator()
                },
                ObjectAnimator.ofFloat(info.frame, View.TRANSLATION_Y, info.dy, 0f).apply {
                    duration = slideDuration
                    interpolator = DecelerateInterpolator()
                }
            )
        }

        val mergeFrames = animInfos.filter { it.isMerge }.map { it.frame }
        val bounceSet = if (mergeFrames.isNotEmpty()) {
            AnimatorSet().apply {
                playTogether(mergeFrames.flatMap { frame ->
                    listOf(
                        ObjectAnimator.ofFloat(frame, View.SCALE_X, 1f, 1.2f, 1f).apply { duration = 120L },
                        ObjectAnimator.ofFloat(frame, View.SCALE_Y, 1f, 1.2f, 1f).apply { duration = 120L }
                    )
                })
            }
        } else null

        val slideSet = AnimatorSet().apply { playTogether(slideAnimators) }

        val fullSet = AnimatorSet()
        if (bounceSet != null) {
            fullSet.playSequentially(slideSet, bounceSet)
        } else {
            fullSet.play(slideSet)
        }
        fullSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                animateNewTile(newTileFrame, onComplete)
            }
        })
        fullSet.start()
    }

    private fun animateNewTile(frame: View?, onComplete: () -> Unit) {
        if (frame == null) {
            onComplete()
            return
        }
        frame.animate()
            .scaleX(1f).scaleY(1f)
            .setDuration(120L)
            .withEndAction { onComplete() }
            .start()
    }

    private fun checkGameOver(): Boolean {
        for (r in 0..3) for (c in 0..3) {
            if (board[r][c] == 0) return false
            if (c < 3 && board[r][c] == board[r][c + 1]) return false
            if (r < 3 && board[r][c] == board[r + 1][c]) return false
        }
        return true
    }

    private fun renderBoard() {
        for (r in 0..3) {
            for (c in 0..3) {
                val value = board[r][c]
                val tile = tileViews[r][c] ?: continue
                if (value == 0) {
                    tile.frame.setBackgroundColor(Color.parseColor("#5D5A50"))
                    tile.image.setImageDrawable(null)
                    tile.image.clearColorFilter()
                    tile.label.text = ""
                } else {
                    tile.frame.setBackgroundColor(Color.parseColor("#3D3A32"))
                    val resId = getTileResId(value)
                    if (resId != 0) {
                        tile.image.setImageResource(resId)
                        val tint = getTileColorTint(value)
                        if (tint != null) {
                            tile.image.setColorFilter(tint, PorterDuff.Mode.MULTIPLY)
                        } else {
                            tile.image.clearColorFilter()
                        }
                    } else {
                        tile.image.setImageDrawable(null)
                        tile.image.clearColorFilter()
                    }
                    tile.label.text = value.toString()
                }
            }
        }
    }

    private fun getTileResId(value: Int): Int {
        val pkg = requireContext().packageName
        val name = when (value) {
            2 -> "img1"
            4 -> "img2"
            8 -> "img3"
            16 -> "img4"
            32 -> "img5"
            64 -> "img6"
            128 -> "epam_big_d"
            256 -> "ic_launcher_foreground"
            512 -> "ic_launcher_background"
            1024 -> "img1"
            2048 -> "img2"
            else -> null
        } ?: return 0
        return requireContext().resources.getIdentifier(name, "drawable", pkg)
    }

    private fun getTileColorTint(value: Int): Int? = when (value) {
        1024 -> Color.parseColor("#FF5722")
        2048 -> Color.parseColor("#FFD700")
        else -> null
    }

    private fun showStatus(message: String) {
        binding.tvGameStatus.text = message
        binding.tvGameStatus.visibility = View.VISIBLE
    }

    private fun updateScoreUi() {
        binding.tvScore.text = score.toString()
        if (score > bestScore) {
            bestScore = score
            binding.tvBestScore.text = bestScore.toString()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("score", score)
        outState.putInt("bestScore", bestScore)
        outState.putBoolean("gameOver", gameOver)
        outState.putBoolean("won", won)
        val flat = IntArray(16)
        for (r in 0..3) for (c in 0..3) flat[r * 4 + c] = board[r][c]
        outState.putIntArray("board", flat)
    }

    private fun restoreState(state: Bundle) {
        score = state.getInt("score")
        bestScore = state.getInt("bestScore")
        gameOver = state.getBoolean("gameOver")
        won = state.getBoolean("won")
        val flat = state.getIntArray("board") ?: return
        for (r in 0..3) for (c in 0..3) board[r][c] = flat[r * 4 + c]
        if (gameOver) showStatus("Game Over")
        else if (won) showStatus("You Win!")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
