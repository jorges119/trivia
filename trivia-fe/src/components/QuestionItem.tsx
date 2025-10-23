import type { Question } from "../models/Question"
import {  Box, ToggleButton, ToggleButtonGroup } from "@mui/material";
import * as he from 'he'
import type { Answer } from "../models/Answer";

interface Props {
	question: Question;
	answer?: string;
	onAnswer: (value: string) => void;
	results?: Answer;
	visible: boolean;
	done: boolean;
}

const QuestionItem = ({question, onAnswer, answer, results, visible,done} : Props) => {

	return(<>{visible && 
		<Box sx={{ p: 2 }}>
			<p>{he.decode(question.question)}</p>
			<ToggleButtonGroup
				orientation="vertical"
				value={answer}
				disabled={done}
				exclusive
				onChange={(_,v) => onAnswer(v)}
			>
				{question.options.map((o) => 
					<ToggleButton key={o} 
						value={o} 
						color={!results? "standard" : (results.correct? "success": "error")}>
							{he.decode(o)}
					</ToggleButton>)
				}
				{done && !results && <>You skipped this question</>}
				{done && results && <>{results.correct} out of {results.asked} contestants got this question right</>}
			</ToggleButtonGroup>
		</Box>
}
	</>)

}

export default QuestionItem