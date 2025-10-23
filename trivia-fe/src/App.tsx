import React from 'react'
import {
  QueryClient,
  QueryClientProvider,
} from '@tanstack/react-query'
import './App.css'
import Questions from './components/Questions'
import { Button, Slider } from '@mui/material'

const queryClient = new QueryClient()

function App() {
  const [amount, setAmount] = React.useState<number>(10)
  const [start, setStart] = React.useState(false)

  return (
    <>
      <h1>Amazing Trivia </h1>
      {!start && 
      <>
        <p>Select number of questions</p>
        <Slider defaultValue={10} max={50} min={1} onChange={(_, val) => setAmount(val)} />
          <p>{amount}</p>
        <Button onClick={() => setStart(true)}>Start</Button>
      </>}
      {start && 
        <QueryClientProvider client={queryClient}>
          <Questions amount={amount} onDone={()=> setStart(false)}/>
        </QueryClientProvider>
      }
    </>
  )
}

export default App
