package tga

class WrongStep(i: Int, msg: String): RuntimeException("$msg [step $i]")