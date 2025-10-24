import React, { useEffect } from "react"
import { useAnswers, useQuestions } from "../hooks/trivia.hooks"
import QuestionItem from "./QuestionItem"
import { BottomNavigation, BottomNavigationAction, Button } from "@mui/material"

interface Props {
	amount: number;
	onDone: () => void;
}

const Questions = ({amount, onDone}: Props) => {
	const {data, isError, isLoading, isSuccess} = useQuestions(amount)
	const [current, setCurrent] = React.useState(0)
	const [finished, setFinished] = React.useState(false)
	const [answers, setAnswers] = React.useState<{[key in string]:string}>({})
	const {data:checkedAnswers, isError:aError, mutate:checkAnswers } = useAnswers(answers)

	useEffect(()=> {
		if (finished){
			checkAnswers()
		}
	}, [finished, checkAnswers])

	return <>
			{finished && <>
				<h2> You got {checkedAnswers?.filter(a => a.success).length} correct answers!  </h2>
				<Button variant='outlined' onClick={() => onDone()}>Start Again</Button>
				</>}
			{(isLoading)? 
			<h1>Loading</h1> : 
			<>
				{(isError || aError || !isSuccess ) ?
					<h1>Try again</h1> : 
					<>
						{data.map((q,qi) => 
							<QuestionItem key={q.hash} 
								answer={answers[q.hash]}
								onAnswer={(a:string) => setAnswers(v => ({...v, [q.hash]: a}))} 
								question={q} 
								results={checkedAnswers?.[qi]}
								done={finished}
								visible={current === qi || finished} />)}
						{!finished &&
							<BottomNavigation
								showLabels
								onChange={(_, newValue) => {
									if (newValue === 'back') setCurrent(v => v-1)
									else if (newValue === 'forward') setCurrent(v => v+1)
									else setFinished(true)
								}}
								>
								{current>0 && <BottomNavigationAction label="Previous"  value="back"/>}
								{Object.keys(answers).length > 0 && <BottomNavigationAction label={`Finish (${Object.keys(answers).length}/${amount})`} value="end" />}
								{current< amount-1 && <BottomNavigationAction label="Next" value="forward"/>}
							</BottomNavigation>
						}
					</>
				}
			</>
		}</> 
}

export default Questions