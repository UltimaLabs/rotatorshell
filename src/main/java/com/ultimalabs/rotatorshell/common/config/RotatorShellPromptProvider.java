package com.ultimalabs.rotatorshell.common.config;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

@Component
public class RotatorShellPromptProvider implements PromptProvider {

    @Override
    public AttributedString getPrompt() {
        return new AttributedString("RotatorShell:> ",
                AttributedStyle.DEFAULT.foreground(AttributedStyle.WHITE)
        );
    }
}