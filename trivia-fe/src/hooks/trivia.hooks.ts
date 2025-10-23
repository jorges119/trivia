import { useQuery, useMutation } from '@tanstack/react-query'
import type { Question } from '../models/Question'
import type { Answer } from '../models/Answer'

let beURL = '' 

if (!process.env.NODE_ENV || process.env.NODE_ENV === 'development') {
	beURL = 'http://localhost:8080'
}

const useQuestions = (amount: number) => {
  return useQuery({
    queryKey: ['questions'],
    queryFn: async (): Promise<Array<Question>> => {
      const response = await fetch(`${beURL}/questions?amount=${amount}`)
      if (response.status != 200) throw new Error('Unexpected code TBI')
      return await response.json()
    },
    refetchOnWindowFocus: false,
    refetchOnReconnect: false,
  })
}

const useAnswers = (answers: {[key in string]: string}) => {
  return useMutation({
    mutationFn: async (): Promise<Array<Answer>> => {
      const response = await fetch(`${beURL}/answers`, {
        method: 'POST', 
        headers: {"content-type": "application/json; charset=utf-8"},
        body: JSON.stringify(
          Object.keys(answers).map(k => 
            ({hash: k, answer: answers[k]})
          ))})
      if (response.status != 201) throw new Error('Unexpected code TBI')
      return await response.json()
    },
  })
}

export { useQuestions, useAnswers }