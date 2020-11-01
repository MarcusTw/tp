package atas.logic.commands;

import atas.commons.core.index.Index;

/**
 * Represents a Command that requires Index or IndexRange.
 */
public interface IndexedCommand extends Command {
    Index getTargetIndex();
}
