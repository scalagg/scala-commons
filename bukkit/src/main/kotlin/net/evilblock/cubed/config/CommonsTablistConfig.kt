package net.evilblock.cubed.config

import xyz.mkotb.configapi.comment.Comment

class CommonsTablistConfig
{
    val enabled = false

    @Comment("Should be true.")
    val continueInitializationIfNullInvokation = false
    val tablistInitializeTicks = 10
}
