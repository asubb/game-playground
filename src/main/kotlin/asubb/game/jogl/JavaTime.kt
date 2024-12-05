package asubb.game.jogl

import asubb.game.Time

class JavaTime : Time {
    override fun getCurrentTime(): Long {
        return System.currentTimeMillis();
    }
}