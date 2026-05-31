import React from 'react';

/**
 * Displays transparency metadata and guardrail warnings.
 */
function TransparencyPanel({ metadata, warnings }) {
  if (!metadata && (!warnings || warnings.length === 0)) {
    return null;
  }

  const getConfidenceBadge = (score) => {
    if (score >= 0.7) return { label: 'High Confidence', class: 'high' };
    if (score >= 0.4) return { label: 'Medium Confidence', class: 'medium' };
    return { label: 'Low Confidence', class: 'low' };
  };

  const getSourceBadge = (source) => {
    const badges = {
      handbook: { label: '📚 Handbook Data', class: 'handbook' },
      'ai-knowledge': { label: '🤖 AI Knowledge', class: 'ai' },
      hybrid: { label: '🔄 Mixed Sources', class: 'hybrid' },
    };
    return badges[source] || badges['ai-knowledge'];
  };

  const getSeverityClass = (severity) => {
    return severity === 'critical' ? 'critical' : severity === 'warning' ? 'warning' : 'info';
  };

  return (
    <div className="transparency-panel">
      {/* Metadata badges */}
      {metadata && (
        <div className="metadata-badges">
          <span className={`badge confidence-${getConfidenceBadge(metadata.confidenceScore).class}`}>
            {getConfidenceBadge(metadata.confidenceScore).label}
          </span>
          <span className={`badge source-${getSourceBadge(metadata.dataSource).class}`}>
            {getSourceBadge(metadata.dataSource).label}
          </span>
          {metadata.relevanceScore > 0 && (
            <span className="badge relevance" title="How relevant the handbook content is to your query">
              📊 {metadata.relevanceScore}% Relevant
            </span>
          )}
          {!metadata.isOnTopic && (
            <span className="badge off-topic">
              ⚠️ Outside Core Topics
            </span>
          )}
        </div>
      )}

      {/* Guardrail warnings */}
      {warnings && warnings.length > 0 && (
        <div className="warnings-list">
          {warnings.map((warning, index) => (
            <div key={index} className={`warning ${getSeverityClass(warning.severity)}`}>
              <p>{warning.message}</p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default TransparencyPanel;
