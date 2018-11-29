/*
 * Copyright 2016-2018 shardingsphere.io.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package io.shardingsphere.core.parsing.antlr.extractor.segment.engine;

import java.util.Map;

import org.antlr.v4.runtime.ParserRuleContext;

import com.google.common.base.Optional;

import io.shardingsphere.core.parsing.antlr.extractor.segment.OptionalSQLSegmentExtractor;
import io.shardingsphere.core.parsing.antlr.extractor.segment.constant.RuleName;
import io.shardingsphere.core.parsing.antlr.extractor.util.ASTUtils;
import io.shardingsphere.core.parsing.antlr.sql.segment.ColumnSegment;
import io.shardingsphere.core.parsing.lexer.token.Symbol;
import lombok.RequiredArgsConstructor;

/**
 * Column extract handler.
 * 
 * @author duhongjun
 */
@RequiredArgsConstructor
public final class ColumnSegmentExtractor implements OptionalSQLSegmentExtractor {
    
    private final Map<String, String> tableAlias;
    
    @Override
    public Optional<ColumnSegment> extract(final ParserRuleContext ancestorNode) {
        Optional<ParserRuleContext> columnNode = ASTUtils.findFirstChildNode(ancestorNode, RuleName.COLUMN_NAME);
        if (!columnNode.isPresent()) {
            return Optional.absent();
        }
        String columnText = columnNode.get().getText();
        int dotPosition = columnText.contains(Symbol.DOT.getLiterals()) ? columnText.lastIndexOf(Symbol.DOT.getLiterals()) : 0;
        String columnName = columnText;
        Optional<String> ownerName;
        String tableName = null;
        if (0 < dotPosition) {
            columnName = columnText.substring(dotPosition + 1);
            ownerName = Optional.of(columnText.substring(0, dotPosition));
            tableName = tableAlias.get(ownerName.get());
        } else {
            ownerName = Optional.absent();
        }
        
        return Optional.of(new ColumnSegment(ownerName, columnName, tableName, columnNode.get().getStart().getStartIndex()));
    }
}
