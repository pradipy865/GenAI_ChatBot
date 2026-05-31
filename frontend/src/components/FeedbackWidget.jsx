import React, { useState } from 'react';

/**
 * Feedback widget for rating responses.
 */
function FeedbackWidget({ messageId, sessionId, onSubmit }) {
  const [showFeedback, setShowFeedback] = useState(false);
  const [submitted, setSubmitted] = useState(false);
  const [rating, setRating] = useState(null);
  const [comment, setComment] = useState('');
  const [category, setCategory] = useState('accuracy');

  const handleSubmit = async () => {
    if (!rating) return;

    await onSubmit({
      messageId,
      sessionId,
      rating,
      comment,
      category,
    });

    setSubmitted(true);
    setTimeout(() => {
      setShowFeedback(false);
      setSubmitted(false);
    }, 2000);
  };

  if (submitted) {
    return <div className="feedback-success">✓ Thank you for your feedback!</div>;
  }

  if (!showFeedback) {
    return (
      <div className="feedback-trigger">
        <button onClick={() => setShowFeedback(true)} className="feedback-ask" title="Was this helpful?">
          Rate this response
        </button>
      </div>
    );
  }

  return (
    <div className="feedback-form">
      <p className="feedback-question">Was this response helpful?</p>
      <div className="feedback-buttons">
        <button
          className={`feedback-btn ${rating === 'helpful' ? 'active' : ''}`}
          onClick={() => setRating('helpful')}
        >
          👍 Yes
        </button>
        <button
          className={`feedback-btn ${rating === 'not-helpful' ? 'active' : ''}`}
          onClick={() => setRating('not-helpful')}
        >
          👎 No
        </button>
      </div>

      {rating && (
        <>
          <select
            value={category}
            onChange={(e) => setCategory(e.target.value)}
            className="feedback-category"
          >
            <option value="accuracy">Accuracy</option>
            <option value="relevance">Relevance</option>
            <option value="clarity">Clarity</option>
            <option value="other">Other</option>
          </select>

          <textarea
            value={comment}
            onChange={(e) => setComment(e.target.value)}
            placeholder="Optional: Tell us more..."
            className="feedback-comment"
            rows="2"
          />

          <div className="feedback-actions">
            <button onClick={handleSubmit} className="feedback-submit">
              Submit
            </button>
            <button onClick={() => setShowFeedback(false)} className="feedback-cancel">
              Cancel
            </button>
          </div>
        </>
      )}
    </div>
  );
}

export default FeedbackWidget;
